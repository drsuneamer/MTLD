import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useNavigate, Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import DeleteModal from 'components/common/DeleteModal';
import { deletePuppyInfo, fetchPuppyInfo, puppySelector } from 'app/puppy';
import Spinner from 'components/common/Spinner';

const Wrap = styled.div`
  margin: 0;
  padding: 0;
`;

const PuppyInfo = styled.div`
  display: flex;
  margin: 2vh;
  height: 20vh;

  .puppyInfo {
    padding-left: 5vw;
  }

  h2 {
    color: #5c5c5c;
    text-align: left;
    margin: 0;
    padding-bottom: 2vh;
  }

  .name-gender {
    display: flex;
  }

  p {
    color: #5c5c5c;
    text-align: left;
    margin: 0;
  }

  .buttonDiv {
    display: flex;
    width: 100%;
    margin-top: 1vh;
  }

  button {
    width: 100%;
    border-radius: 5px;
    height: 1.5rem;
    margin-right: 1vw;
    border: none;
    background-color: #ffeeb1;
  }
`;

const PuppyImage = styled.img`
  height: 20vh;
  width: 20vh;
  border: 3px solid #ffeeb1;
  border-radius: 10px;
  overflow: hidden;
  object-fit: cover;
`;

function RegisteredPet() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const puppy = useSelector(puppySelector);
  const [myPage, setMyPage] = useState([]);

  useEffect(() => {
    dispatch(fetchPuppyInfo());
  }, []);

  if (!puppy.loading) {
    return <Spinner />;
  }

  if (puppy.puppyInfo.length === 1) {
    return (
      <Wrap>
        <PuppyInfo>
          <div className="puppyImage">
            <PuppyImage src={puppy.puppyInfo[0].fileURL} alt="" />
          </div>
          <div className="puppyInfo">
            <div className="name-gender">
              <h2>{puppy.puppyInfo[0].name}</h2>
              <h2>&#40;{puppy.puppyInfo[0].gender}&#41;</h2>
            </div>
            <p>{puppy.puppyInfo[0].breedName}</p>
            {/* 날짜 파싱은 나중에... ^^...  */}
            <p>{puppy.puppyInfo[0].birthdate}일 생</p>
            <div className="buttonDiv">
              <Link to="/pet-info-detail">
                <button type="button">세부정보</button>
              </Link>
              <Link to="/mypage">
                <button
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[0].id));
                    alert('삭제되었습니다');
                  }}
                  type="button"
                  style={{ color: '#F38181' }}
                >
                  삭제하기
                </button>
              </Link>
            </div>
          </div>
        </PuppyInfo>
      </Wrap>
    );
  }
  if (puppy.puppyInfo.length === 2) {
    return (
      <div>
        <Wrap>
          <PuppyInfo>
            <div className="puppyImage">
              <PuppyImage src={puppy.puppyInfo[0].fileURL} alt="" />
            </div>
            <div className="puppyInfo">
              <div className="name-gender">
                <h2>{puppy.puppyInfo[0].name}</h2>
                <h2>&#40;{puppy.puppyInfo[0].gender}&#41;</h2>
              </div>
              <p>{puppy.puppyInfo[0].breedName}</p>
              {/* 날짜 파싱은 나중에... ^^...  */}
              <p>{puppy.puppyInfo[0].birthdate}일 생</p>
              <div className="buttonDiv">
                <button type="button">세부정보</button>
                <button
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[0].id));
                    window.alert('성공적으로 삭제되었습니다');
                    navigate('/mypage');
                  }}
                  type="button"
                  style={{ color: '#F38181' }}
                >
                  삭제하기
                </button>
              </div>
            </div>
          </PuppyInfo>
        </Wrap>
        <Wrap>
          <PuppyInfo>
            <div className="puppyImage">
              <PuppyImage src={puppy.puppyInfo[1].fileURL} alt="" />
            </div>
            <div className="puppyInfo">
              <div className="name-gender">
                <h2>{puppy.puppyInfo[1].name}</h2>
                <h2>&#40;{puppy.puppyInfo[1].gender}&#41;</h2>
              </div>
              <p>{puppy.puppyInfo[1].breedName}</p>
              {/* 날짜 파싱은 나중에... ^^...  */}
              <p>{puppy.puppyInfo[1].birthdate}일 생</p>
              <div className="buttonDiv">
                <button type="button">세부정보</button>
                <button
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[1].id));
                    window.alert('성공적으로 삭제되었습니다');
                    navigate('/mypage');
                  }}
                  type="button"
                  style={{ color: '#F38181' }}
                >
                  삭제하기
                </button>
              </div>
            </div>
          </PuppyInfo>
        </Wrap>
      </div>
    );
  }
  if (puppy.puppyInfo.length === 3) {
    return (
      <div>
        <Wrap>
          <PuppyInfo>
            <div className="puppyImage">
              <PuppyImage src={puppy.puppyInfo[0].fileURL} alt="" />
            </div>
            <div className="puppyInfo">
              <div className="name-gender">
                <h2>{puppy.puppyInfo[0].name}</h2>
                <h2>&#40;{puppy.puppyInfo[0].gender}&#41;</h2>
              </div>
              <p>{puppy.puppyInfo[0].breedName}</p>
              {/* 날짜 파싱은 나중에... ^^...  */}
              <p>{puppy.puppyInfo[0].birthdate}일 생</p>
              <div className="buttonDiv">
                <button type="button">세부정보</button>
                <button
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[0].id));
                    window.alert('성공적으로 삭제되었습니다');
                    navigate('/mypage');
                  }}
                  type="button"
                  style={{ color: '#F38181' }}
                >
                  삭제하기
                </button>
              </div>
            </div>
          </PuppyInfo>
        </Wrap>
        <Wrap>
          <PuppyInfo>
            <div className="puppyImage">
              <PuppyImage src={puppy.puppyInfo[1].fileURL} alt="" />
            </div>
            <div className="puppyInfo">
              <div className="name-gender">
                <h2>{puppy.puppyInfo[1].name}</h2>
                <h2>&#40;{puppy.puppyInfo[1].gender}&#41;</h2>
              </div>
              <p>{puppy.puppyInfo[1].breedName}</p>
              {/* 날짜 파싱은 나중에... ^^...  */}
              <p>{puppy.puppyInfo[1].birthdate}일 생</p>
              <div className="buttonDiv">
                <button type="button">세부정보</button>
                <button
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[1].id));
                    window.alert('성공적으로 삭제되었습니다');
                    navigate('/mypage');
                  }}
                  type="button"
                  style={{ color: '#F38181' }}
                >
                  삭제하기
                </button>
              </div>
            </div>
          </PuppyInfo>
        </Wrap>
        <Wrap>
          <PuppyInfo>
            <div className="puppyImage">
              <PuppyImage src={puppy.puppyInfo[2].fileURL} alt="" />
            </div>
            <div className="puppyInfo">
              <div className="name-gender">
                <h2>{puppy.puppyInfo[2].name}</h2>
                <h2>&#40;{puppy.puppyInfo[2].gender}&#41;</h2>
              </div>
              <p>{puppy.puppyInfo[2].breedName}</p>
              {/* 날짜 파싱은 나중에... ^^...  */}
              <p>{puppy.puppyInfo[2].birthdate}일 생</p>
              <div className="buttonDiv">
                <button type="button">세부정보</button>
                <button
                  type="button"
                  style={{ color: '#F38181' }}
                  onClick={() => {
                    dispatch(deletePuppyInfo(puppy.puppyInfo[2].id));
                    window.alert('성공적으로 삭제되었습니다');
                    navigate('/mypage');
                  }}
                >
                  삭제하기
                </button>
              </div>
            </div>
          </PuppyInfo>
        </Wrap>
      </div>
    );
  }
}

export default RegisteredPet;
