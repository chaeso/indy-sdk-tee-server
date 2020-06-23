# Indy-SDK-TEE-Server Demo

### 소개

외부 TEE 환경을 적용할 수 있도록 수정한 indy-sdk을 서버환경의 Java 어플리케이션 데모입니다. 

### 설치방법 
1. SoftHSM에 기반한 서버 시작
2. TEE 주입 기능이 추가된 Indy-SDK 빌드하여 src/lib/<YOUR_PLATFORM>/<OUTPUT_LIB_FILENAME>
3. TEE 주입 기능이 추가된 Indy-SDK 의 Java Wrapper를 빌드하여 src/lib/indy-1.**.*.jar 를 복사한다.
4. maven 빌드하고 실행 
