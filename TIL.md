# Today I Learned

## 2. 서블릿

### 2-1. 프로젝트 생성

#### build.gradle

```
plugins {
	id 'org.springframework.boot' version '2.5.0'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
	id 'war'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
	useJUnitPlatform()
}

```

* Spring version : 2.5.0
* java : 11
* JSP를 학습하기 위해서 `jar`대신 `war`를 선택
* `Lombok`과 `Spring web`을 의존성에 추가

#### 설정 변경

* `Setting` -> `gradle`에서 `Build and run using`, `Run test using` 항목을 `Intellij IDEA`로 설정
  ![](https://i.ibb.co/RTMbmpw/bandicam-2021-06-02-18-10-58-048.jpg)

* 롬복 플러그인 설치



* `Annotation Processors`에서 `Enable annotaion processing` 항목 체크
  ![](https://i.ibb.co/BHJ4CyP/bandicam-2021-06-02-18-13-09-187.jpg)