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
    * **주의!! IntelliJ 무료버전일때 `War`의 경우 톰캣이 정상 시작되지 않는 경우가 생김**
    * 이땐 build를 gradle로 다시 설정하거나
    * `build.gradle`에서 `provideRuntime 'org.springframework.boot:spring-boot-starter-tomcat`을 제거해야 한다. (이 방법을 선택)
        * `build and run`을 Gradle로 설정하면 콘솔창에서 한글 출력이 잘 되지 않는 경우가 생김

* 롬복 플러그인 설치


* `Annotation Processors`에서 `Enable annotaion processing` 항목 체크
  ![](https://i.ibb.co/BHJ4CyP/bandicam-2021-06-02-18-13-09-187.jpg)

### 2-2. Hello 서블릿

스프링 부트 환경에서 서블릿을 등록하고 사용하기

> **참고**<br>
> 서블릿은 톰캣 같은 웹 어플리케이션 서버를 직접 설치하고, 그 위에 서블릿 코드를 클래스 파일로 빌드해서 올린 다음, 톰캣 서버를 실행하면 된다. 하지만 이 과정은 매우 번거롭다.<br>
> 스프링 부트는 톰캣 서버를 내장하고 있으므로, 톰캣 서버 설치 없이 편리하게 서블릿 코드를 실행할 수 있다.

#### 스프링 부트 서블릿 환경 구성

`@ServletComponentScan`
스프링 부터는 서블릿을 직접 등록해서 사용할 수 있도록 `@ServletComponentScan`을 지원한다.

```java
package hello.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ServletApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServletApplication.class, args);
    }
}

```

#### 서블릿 등록하기

```java
package hello.servlet.basic;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username = " + username);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("hello " + username);

    }
}

```

* `@WebServlet`서블릿 애노테이션
    * name: 서블릿 이름
    * urlPatterns: URL 매핑

* HTTP 요청을 통해 매핑된 URL이 호출되면 서블릿 컨테이너는 다음 메서드를 실행한다.   
  `protected void service(HttpServletRequest request, HttpServletResponse response)`
    * `response.getParameter("username");`
        * `response`메시지에서 쿼리 파라미터의 `username`값을 읽는다.
    * `response.setContestType("text/plain);`, `response.setCharacterEncoding("utf-8");`
        * HTTP header의 내용을 설정한다.
        * 최근에는 인코딩은 대부분 UTF-8을 이용하는 것이 좋다.
    * `response.getWriter().writer("hello " + username);`
        * `response` message 내용을 직접 넣어주었다.
        * `request`에서 받아온 `username`을 다시 `response`의 메시지 내용에 저장

* 웹 브라우저 실행
    * [http://localhost:8080/hello?username=world](http://localhost:8080/hello?username=world)
    * 결과: hello world
    * 콘솔 실행 결과

#### HTTP 요청 메시지 로그로 확인하기

`application.properties`에 다음 코드를 추가한다.

```
logging.level.org.apache.coyote.http11=debug
```

* 로그 결과
  ![](https://i.ibb.co/S0Zh78R/bandicam-2021-06-02-19-35-43-911.jpg)

> 참고<br>
> 운영서버에 이렇게 모든 요청 정보를 다 남기면 성능저하가 발생할 수 있다. 개발 단계에서만 적용하는 것이 좋다.

#### welcome 페이지 추가

* `webapp`경로에 `index.html`을 두면 http://localhost:8080 호출시 `index.html` 페이지가 열린다.
* **html에 대한 내용은 생략하겠다.**


* `main/webapp/index.html`
    ```html
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    <ul>
        <li><a href="basic.html">서블릿 basic</a></li>
    </ul>
    </body>
    </html>
    ```

* `main/webapp/basic.html`

    ```html
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    <ul>
        <li>hello 서블릿
            <ul>
                <li><a href="/hello?username=servlet">hello 서블릿 호출</a></li>
            </ul>
        </li>
        <li>HttpServletRequest
            <ul>
                <li><a href="/request-header">기본 사용법, Header 조회</a></li>
                <li>HTTP 요청 메시지 바디 조회
                    <ul>
                        <li><a href="/request-param?username=hello&age=20">GET -
                            쿼리 파라미터</a></li>
                        <li><a href="/basic/hello-form.html">POST - HTML Form</a></
                        li>
                        <li>HTTP API - MessageBody -> Postman 테스트</li>
                    </ul>
                </li>
            </ul>
        </li>
        <li>HttpServletResponse
            <ul>
                <li><a href="/response-header">기본 사용법, Header 조회</a></li>
                <li>HTTP 요청 메시지 바디 조회
                    <ul>
                        <li><a href="/response-html">HTML 응답</a></li>
                        <li><a href="/response-json">HTTP API JSON 응답</a></li>
                    </ul>
                </li>
            </ul>
        </li>
    </ul>
    </body>
    </html>
    ```

# Note

* IntelliJ 무료버전일때 `War`의 경우 톰캣이 정상 시작되지 않는 경우가 생김
    * 이땐 build를 gradle로 다시 설정하거나
    * `provideRuntime 'org.springframework.boot:spring-boot-starter-tomcat`을 제거해야 한다.