package com.mtld.backend.service.diary;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mtld.backend.dto.diary.DiaryResponseDto;
import com.mtld.backend.dto.diary.record.RecordDetailResponseDto;
import com.mtld.backend.dto.diary.record.RecordRequestDto;
import com.mtld.backend.dto.diary.walking.WalkingDetailRequestDto;
import com.mtld.backend.dto.diary.walking.WalkingDetailResponseDto;
import com.mtld.backend.dto.diary.walking.WalkingRequestDto;
import com.mtld.backend.entity.UploadFile;
import com.mtld.backend.entity.user.User;
import com.mtld.backend.entity.diary.Record;
import com.mtld.backend.entity.diary.Walking;
import com.mtld.backend.entity.dog.Dog;
import com.mtld.backend.exception.AuthException;
import com.mtld.backend.exception.BadRequestException;
import com.mtld.backend.exception.NoContentException;
import com.mtld.backend.repository.dog.DogRepository;
import com.mtld.backend.repository.UploadFileRepository;
import com.mtld.backend.repository.diary.RecordRepository;
import com.mtld.backend.repository.diary.WalkingRepository;
import com.mtld.backend.repository.user.UserRepository;
import com.mtld.backend.util.ConvertDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * created by seongmin on 2022/09/13
 * updated by seongmin on 2022/09/21
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryServiceImpl implements DiaryService {

    private final WalkingRepository walkingRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final UploadFileRepository uploadFileRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    @Override
    @Transactional
    public Long writeWalking(Long uid, WalkingRequestDto walkingDto) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        Dog dog = dogRepository.findById(walkingDto.getDogId()).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        LocalDate diaryDate = ConvertDate.stringToDate(walkingDto.getDiaryDate());
        validDate(diaryDate);
        if (walkingRepository.findByDiaryDateBetweenAndDog(diaryDate, diaryDate, dog).isPresent()) {
            throw new BadRequestException("?????? ????????????.");
        }

        Walking walking = Walking.builder()
                .walkingTime(walkingDto.getWalkingTime())
                .diaryDate(diaryDate)
                .distance(walkingDto.getDistance())
                .dog(dog)
                .user(user)
                .build();
        walkingRepository.save(walking);
        return walking.getId();
    }

    @Override
    public WalkingDetailResponseDto getWalkingDetail(Long uid, Long dogId, String date) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));

        LocalDate diaryDate = ConvertDate.stringToDate(date);

        Dog dog = dogRepository.findByIdAndUser(dogId, user).orElseThrow(() -> new AuthException("????????? ????????????."));
        Walking result = walkingRepository.findByDiaryDateBetweenAndDog(diaryDate, diaryDate, dog).orElseThrow(() -> new NoContentException("???????????? ?????? ?????????????????????."));
        return WalkingDetailResponseDto.of(result);
    }

    @Override
    public WalkingDetailResponseDto getWalkingDetailById(Long uid, Long id) {
        log.info("uid = {}" , uid);
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        Walking walking = walkingRepository.findById(id).orElseThrow(() -> new NoContentException("???????????? ?????? ?????????????????????."));
        if (!walking.getUser().equals(user)) {
            throw new AuthException("????????? ????????????.");
        }
        return WalkingDetailResponseDto.of(walking);
    }

    @Transactional
    @Override
    public void deleteWalking(Long uid, Long id) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        Walking walking = walkingRepository.findById(id).orElseThrow(() -> new BadRequestException("???????????? ?????? ?????????????????????."));
        if (!walking.getUser().equals(user)) {
            throw new AuthException("?????? ????????? ????????????.");
        }
        walkingRepository.delete(walking);
    }

    @Override
    @Transactional
    public Long writeRecord(Long uid, RecordRequestDto recordDto, List<MultipartFile> images) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        LocalDate diaryDate = ConvertDate.stringToDate(recordDto.getDiaryDate());
        validDate(diaryDate);
        if (recordRepository.findByDiaryDateBetweenAndUser(diaryDate, diaryDate, user).isPresent()) {
            throw new BadRequestException("?????? ????????????.");
        }

        List<String> filePathList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();

        try {
            images.forEach(image -> {
                String originName = image.getOriginalFilename();
                String fileName = createFileName(originName);
                fileNameList.add(fileName);

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(image.getContentType());
                objectMetadata.setContentLength(image.getSize());

                try (InputStream inputStream = image.getInputStream()) {
                    amazonS3Client.putObject(
                            new PutObjectRequest(bucket, fileName, inputStream,
                                    objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead)
                    );
                    String imagePath = amazonS3Client.getUrl(bucket, fileName).toString();
                    filePathList.add(imagePath);
                } catch (IOException e) {
                    throw new IllegalStateException("?????? ???????????? ??????????????????.");
                }

            });
        } catch (NullPointerException e) {
            log.info("????????? ?????? ??????");
        }

        List<UploadFile> uploadFiles = new ArrayList<>();
        for (int i = 0; i < fileNameList.size(); i++) {
            uploadFiles.add(UploadFile.builder()
                    .name(fileNameList.get(i))
                    .url(filePathList.get(i))
                    .build());
        }
        Record record = Record
                .builder()
                .diaryDate(diaryDate)
                .mainText(recordDto.getMainText())
                .user(user)
                .build();
        recordRepository.save(record);
        uploadFiles.forEach(record::addUploadFile);
        return record.getId();
    }

    @Override
    public RecordDetailResponseDto getRecordDetailByDate(Long uid, String date) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        LocalDate diaryDate = ConvertDate.stringToDate(date);
        Record record = recordRepository.findByDiaryDateBetweenAndUser(diaryDate, diaryDate, user).orElseThrow(() -> new NoContentException("???????????? ?????? ????????????(??????)?????????."));

        return RecordDetailResponseDto.of(record);
    }

    @Override
    public RecordDetailResponseDto getRecordDetailById(Long uid, Long id) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        Record record = recordRepository.findById(id).orElseThrow(() -> new NoContentException("???????????? ?????? ????????????(??????)?????????."));
        if (!record.getUser().equals(user)) {
            throw new AuthException("????????? ????????????.");
        }
        return RecordDetailResponseDto.of(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long uid, Long id) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        Record record = recordRepository.findById(id).orElseThrow(() -> new BadRequestException("???????????? ?????? ????????????(??????)?????????."));
        if (!record.getUser().equals(user)) {
            throw new AuthException("????????? ????????????.");
        }
        for (UploadFile uploadFile : record.getUploadFiles()) {
            amazonS3Client.deleteObject(bucket, uploadFile.getName());
        }
//        uploadFileRepository.deleteAllByRecordId(record);
        recordRepository.delete(record);
    }

    @Override
    public DiaryResponseDto getMyDiaryDate(Long uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadRequestException("???????????? ?????? ??????????????????."));
        List<Walking> walkings = walkingRepository.findByUser(user);
        List<Record> records = recordRepository.findByUser(user);
        return DiaryResponseDto.of(records, walkings);
    }

    // ???????????? ?????? ?????? ??????
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new BadRequestException("????????? ????????? ?????? ?????????");
        }
    }

    private void validDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException("?????? ?????? ???????????? ?????? ???????????????.");
        }
    }
}
