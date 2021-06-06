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

##### ServletApplication

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

##### HelloServlet

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

#### index.html

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

### 2-3. HTTPServletRequest - 개요

#### HTTPServletRequest 역할

HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다. 서블릿은 개발자가 HTTP 요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP 요청 메시지를 파싱한다. 그리고 그
결과를 `HttpServletRequest`객체에 담아서 제공한다.

#### HTTP 요청 메시지

```
POST /save HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username-kim&age=20
```

* START LINE
    * HTTP 메소드
    * URL
    * 쿼리 스트링
    * 스키마, 프로토콜

* 헤더
    * 헤더 조회

* 바디
    * form 파라미터 형식 조회
    * message body 데이터 직접 조회

* `HTTPServletRequest`객체는 추가로 여러가지 부가기능도 함께 제공한다.

#### 임시 저장소 기능

* 해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능
    * 저장: `request.setAttribute(name, value)`
    * 조회: `request.getAttrubute(name)`

#### 세션 관리 기능

* `request.getSession(create: true)`

> **중요!!**<br>
> `HTTPServletRequest`, `HttpServletResponse`를 사용할 때 가장 중요한 점은 이 객체들이 HTTP 요청 메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체라는 점이다. 따라서 이 기능에 대해서 깊이있는 이해를 하려면 **HTTP 스펙이 제공하는 요청, 응답 메시지 자체를 이해**해야 한다.

### 2-4. HTTPServletRequest - 기본 사용법

이 장에서는 일차원적으로 `HTTPServletRequest`객체가 지원하는 메소드를 소개한다. 대부분 HTML 문서에서 원하는 정보를 get형식으로 뽑아오는 경우가 대부분이다. 따라서 부가적인 설명은 생략하고
코드와 결과창만 기록한다.

#### RequestHeaderServlet

* hello.servlet.basic.request.RequestHeaderServlet
  ```java
  package hello.servlet.basic.request;
  
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.Cookie;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.util.Enumeration;
  
  @WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
  public class RequestHeaderServlet extends HttpServlet {
  
      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          printStartLine(request);
          printHeaders(request);
          printHeaderUtils(request);
          printEtc(request);
      }
  }
  
  ```

#### start-line 정보

```java
public class RequestHeaderServlet extends HttpServlet {
    private void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + request.getMethod());
        System.out.println("request.getProtocal() = " + request.getProtocol());
        System.out.println("request.getScheme() = " + request.getScheme());
        System.out.println("request.getRequestURL() = " + request.getRequestURL());
        System.out.println("request.getRequestURI() = " + request.getRequestURI());
        System.out.println("request.getQueryString() = " + request.getQueryString());
        System.out.println("request.isSecure() = " + request.isSecure());
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }
}
```

* QueryParameter를 QueryString이라고 표현하기도 한다.

* 결과  
  ![](https://i.ibb.co/KGJbZ4v/bandicam-2021-06-02-22-30-53-806.jpg)

#### 헤더 정보

```java
public class RequestHeaderServlet extens HttpServlet {
    private void printHeaders(HttpServletRequest request) {
        System.out.println("--- Headers - start ---");

        // Enumeration<String> headerNames = request.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        //     String headerName = headerNames.nextElement();
        //     System.out.println(headerName+": " + request.getHeader(headerName));
        // }

        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + ": " + request.getHeader(headerName)));

        System.out.println("--- Headers - end ---");
        System.out.println();
    }
}
```

* `request.getHeaderNames()`
    * 옛날에 주로 이용했던 방식
    * `JDK 1.5`부터 추가된 Enhanced for문인 `forEachRemaining`을 이용해서 간단하게 코드 작성이 가능하다.
        * 자바에서는 Enhance for문을 이용할것을 권장하지만 일반적인 for loop가 속도는 더 빠르다.

* 결과
  ![](https://i.ibb.co/kJF56GD/bandicam-2021-06-02-22-36-27-353.jpg)

#### Header 편리한 조회

```java
public class RequestHeaderServlet extends HttpServlet {
    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 start ---");
        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " +
                request.getServerName());
        System.out.println("request.getServerPort() = " +
                request.getServerPort());
        System.out.println();
        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> System.out.println("locale = " +
                        locale));
        System.out.println("request.getLocale() = " + request.getLocale());
        System.out.println();
        System.out.println("[cookie 편의 조회]");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
        System.out.println();
        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " +
                request.getContentType());
        System.out.println("request.getContentLength() = " + request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " +
                request.getCharacterEncoding());
        System.out.println("--- Header 편의 조회 end ---");
        System.out.println();
    }
}

```

* 결과  
  ![](https://i.ibb.co/0n3mYGL/bandicam-2021-06-02-22-39-12-327.jpg)

#### 기타 정보

```java
public class RequestHeaderServlet extends HttpServlet {
    private void printEtc(HttpServletRequest request) {
        System.out.println("--- 기타 조회 start ---");
        System.out.println("[Remote 정보]");
        System.out.println("request.getRemoteHost() = " +
                request.getRemoteHost());
        System.out.println("request.getRemoteAddr() = " +
                request.getRemoteAddr());
        System.out.println("request.getRemotePort() = " +
                request.getRemotePort());
        System.out.println();
        System.out.println("[Local 정보]");
        System.out.println("request.getLocalName() = " +
                request.getLocalName());
        System.out.println("request.getLocalAddr() = " +
                request.getLocalAddr());
        System.out.println("request.getLocalPort() = " +
                request.getLocalPort());
        System.out.println("--- 기타 조회 end ---");
        System.out.println();
    }
}
```

* 결과  
  ![](https://i.ibb.co/ScV0yby/bandicam-2021-06-02-22-41-28-359.jpg)

> 참고  
> 로컬에서 테스트하면 IPv6 정보가 나오는데, IPv4 정보를 보고 싶으면 다음 옵션을 VM options에 넣어주면 된다.
> `-Djava.net.preferIPv4Stack=true`

### 2-5. HTTP 요청 데이터 - 개요

* GET - 쿼리 파라미터
    * /url **?username=hello&age=20**
    * 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
    * 예) 검색, 필터, 페이징등에서 많이 사용하는 방식

* POST - HTML Form
    * content-type: application/x-www-form-urlencoded
    * 메시지 바디에 쿼리 파라미터 형식으로 전달 username-hello&age=20
    * 예) 회원 가입, 상품 주문, HTML Form 사용

* HTTP message body 에 데이터를 직접 담아서 요청
    * HTTP API에서 주로 사용, JSON, XML, TEXT

* 데이터 형식은 주로 JSON 사용
    * POST, PUT, PATCH

### 2-6. HTTP 요청 데이터 - GET 쿼리 파라미터

* 전달 데이터
    * username=hello
    * age=20

* 메시지 바디 없이, URL의 쿼리 파라미터를 사용해서 데이터를 전달한다.
* 예) 검색, 필터, 페이징등에서 많이 사용하는 방식

#### RequestParamServlet

```java
package hello.servlet.basic.request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 1. 파라미터 전송 기능
 * http://localhost:8080/request-param?username=hello&age=20
 */
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[전체 파라미터 조회] - start");
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> System.out.println(paramName + "=" + request.getParameter(paramName)));
        System.out.println("[전체 파라미터 조회] - end");
        System.out.println();
        request.getParameterNames();

        System.out.println("[단일 파라미터 조회]");
        String username = request.getParameter("username");
        String age = request.getParameter("age");

        System.out.println("username = " + username);
        System.out.println("age = " + age);
        System.out.println();

        System.out.println("[이름이 같은 복수 파라미터 조회]");
        String[] usernames = request.getParameterValues("username");
        for (String name : usernames) {
            System.out.println("username = " + name);
        }
        System.out.println();

        response.getWriter().write("ok");
    }
}

```

* 실행 - 파라미터 전송 (동일한 파라미터 전송 - username)
    * http://localhost:8080/request-param?username=hello&age=20&username=kim
    * 결과
      ```
      [전체 파라미터 조회] - start
      username=hello
      age=20
      [전체 파라미터 조회] - end
      
      [단일 파라미터 조회]
      request.getParameter(username) = hello
      request.getParameter(age) = 20
      
      [이름이 같은 복수 파라미터 조회]
      request.getParameterValues(username)
      username=hello
      username=kim
      ```

#### 복수 파라미터에서 단일 파라미터 조회

`username=hello&username=kim`과 같이 파라미터 이름은 하나인데, 값이 중복이면 어떨게 될까?  
`request.getParameter()`는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다.   
지금처럼 중복일 때는 `request.getParameterValues()`를 사용해야 한다.    
참고로 이렇게 중복일 때 `request.getParameter()`를 사용하면 `request.getParameterValues()`의 첫 번째 값을 반환한다. (사실 파라미터 값을 중복되게 사용하는 경우는 거의
없다.)

### 2-7. HTTP 요청 데이터 - POST HTML Form

HTML의 Form을 사용해서 클라이언트에서 서버로 데이터 전송하기

#### hello-form.html

* content-type: `application/x-www-form-urlencoded`
* 메시지 바디에 쿼리 파라미터 형식으로 데이터를 전달한다. `username=hello&age=20`
* `src/main/webapp/basic/hello-form.html`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action="/request-param" method="post">
    username: <input type="text" name="username"/>
    age: <input type="text" name="age"/>
    <button type="submit">전송</button>
</form>
</body>
</html>
```

* 실행
    * http://localhost:8080/basic/hello-form.html

> 주의  
> 웹 브라우저가 결과를 캐시하고 있어서, 과거에 결과가 보이는 경우도 있다. 이때는 웹 브라우저의 새로 고침을 직접 선택해주면 된다. 물론 서버를 재시작 하지 않아서 그럴 수도 있다.

* POST의 HTML Form을 전송하면 웹 브라우저는 다음 형식으로 HTTP 메시지를 만든다. (웹 브라우저 개발자 모드 확인)
    * 요청 URL: http://localhost:8080/request-param
    * content-type: `application/x-www-form-urlencoded`
    * message body: `username=hello&age=20`

`application/x-www-form-urlencoded`형식은 앞서 GET에서 살펴본 쿼리 파라미터 형식과 같다. 따라서 **쿼리 파라미터 조회 메서드를 그대로 사용**하면 된다.  
클라이언트(웹 브라우저)입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 둘의 형식이 동일하므로, `request.getParameter()`로 편리하게 구분없이 조회할 수 있다.


> 정리  
> `request.getParameter()`는 GET URL 쿼리 파라미터 형식도 지원하고, POST HTML Form 형식도 둘 다 지원한다.

> 참고  
> content-type은 HTTP 메시지 바디의 데이터 형식을 지정한다.  
> **GET URL 쿼리 파라미터 형식**으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하지 않기 때문에 content-type이 없다.
> **POST HTML Form형식**으로 데이터를 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에 바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다. 이렇게 폼으로 데이터를 전송하는 형식을 `application/x-www-form-urlencoded`라 한다.

#### Postman을 사용한 테스트

이런 간단한 테스트에 HTML form(`hello-form.html`)을 만들기는 귀찮다. 이때는 Postman을 사용하면 된다.

##### Postman 테스트 주의사항

* POST 전송시
    * Body -> `x-www-form-urlencoded`선택
    * Headers에서 content-type:`application/x-www-form-urlencoded`로 지정된 부분 꼭 확인!!

### 2-8. HTTP 요청 데이터 - API 메시지 바디 - 단순 텍스트

* **HTTP message body**에 데이터를 직접 담아서 요청
    * HTTP API에서 주로 사용, JSON, XML, TEXT
    * 데이터 형식을 주로 JSON 사용
    * POST, PUT PATCH

* 먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고 읽어보자.
* HTTP 메시지 바디의 데이터를 `InputStream`을 사용해서 직접 읽을 수 있다.
* 사실 자주 쓰이는 방식은 아니다. (단순 텍스트 보다는 JSON 형식을 많이 쓴다.)

#### RequestBodyStringServlet

```java
package hello.servlet.basic.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}

```

* `StreamUtils`
    * Spring에서 제공하는 util
    * 스트림을 처리하기 위한 간단한 유틸리티 메서드이다.
    * `copyToString`메소드를 이용해서 바이트 코드 형태의 `inputStream`을 String 형태로 변환하였다.

    ```java
    package hello.servlet;
    
    import org.hamcrest.MatcherAssert;
    import org.junit.jupiter.api.Test;
    import org.springframework.util.StreamUtils;
    
    import java.io.ByteArrayInputStream;
    import java.io.InputStream;
    import java.nio.charset.Charset;
    
    import static org.hamcrest.Matchers.equalTo;
    import static org.mockito.Mockito.*;
    
    public class TestCode {
        private String string = "abcdefg";
    
        @Test
        public void copyToStringTest() throws Exception {
            Charset charset = Charset.defaultCharset();
            InputStream inputStream = spy(new ByteArrayInputStream(string.getBytes(charset)));
            String actual = StreamUtils.copyToString(inputStream, charset);
            System.out.println("actual = " + actual);
            MatcherAssert.assertThat(actual, equalTo(string));
            verify(inputStream, never()).close();
        }
    }
    
    ```
    * 임의의 문자열을 바이트 문자열로 변환하고 그 문자열을 다시 String 형태로 변환하여 비교한 테스트 코드 (예제 코드)

> 참고  
> inputStream은 byte 코드를 반환한다. byte 코드를 우리가 읽을 수 있는 문자(String)로 보려면 문자표(Charset)를 지정해주어야 한다. 여기서는 UTF-8 Charset을 지정해 주었다.

* 문자 전송
    * POST http://localhost:8080/request-body-string
    * content-type: text/plain
    * message body: `hello`
    * 결과: `messageBody = hello`

### 2-9. HTTP 요청 데이터 - API 메시지 바디 - JSON

#### JSON 형식 전송

* POST http://localhost:8080/request-body-json
* content-type: application/json
* message body: `{"username": "hello", "age": 20}`
* 결과: `messageBody = {"username": "hello", "age": 20}`

#### JSON 형식 파싱 추가

##### HelloData

* JSON 형식으로 파싱할 수 있게 객체를 하나 생성
* `src/main/java/hello/servlet/basic/HelloData.java`

```java
package hello.servlet.basic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelloData {

    private String username;
    private int age;
}

```

#### RequestBodyJsonServlet

*`src/main/java/hello/servlet/basic/request/RequestBodyJsonServlet.java`

```java
package hello.servlet.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.servlet.basic.HelloData;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * http://localhost:8080/request-body-json
 *
 * JSON 형식 전송
 * content-type: application/json
 * message body: {"username": "hello", "age": "20"}
 */

@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);

        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());

        response.getWriter().write("ok");
    }
}

```

* `ObjectMapper`
    * JSON 형식의 문서를 자바의 객체에 파싱하여 저장할 수 있도록 하는 클래스
    * `Jackson` 라이브러리
    * `readValue()`
        * JSON의 키를 필드로 가지고 있는 객체에 매핑하여 JSON을 객체로 관리할 수 있도록 한다.
        * 파싱을 할 클래스를 파라미터로 가지고 그 클래스를 반환값으로 가진다.
        * `HelloData`클래스는 `username`과 `age`이름을 가진 변수를 필드에 가지고 있다.
        * JSON의 키를 클래스의 필드의 변수명과 매핑하여 해당 변수명에 JSON의 value를 저장한다.

#### Postman으로 실행

* POST http://localhost:8080/request-body-json
* content-type:**application/json**(Body->raw, 가장 오른쪽에서 JSON 선택)
* message body: `{"username": "hello", "age":20}`
* 출력 결과
  ```
  messageBody={"username": "hello", "age": 20}
  data.username=hello
  data.age=20
  ```

> 참고  
> JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변환하려면 Jackson, Gson 같은 JSON 변환 라이브러리를 추가해서 사용해야 한다. 스프링 부트로 Spring MVC를 선택하면 기본으로 Jackson 라이브러리`(ObjectMapper)`를 함께 제공한다.

> 참고  
> HTML Form 데이터도 메시지 바디를 통해 전송되므로 직접 읽을 수 있다. 하지만 편리한 파라미터 조회기능(`request.getParameter(...)`)을 이미 제공하기 때문에 파라미터 조회 기능을 사용하면 된다.

### 2-10. HttpServletResponse - 기본 사용법

#### HttpServletResponse 역할

* HTTP 응답 메시지 생성
    * HTTP 응답코드 지정
    * 헤더 생성
    * 바디 생성

* 편의 기능 제공
    * Content-Type, 쿠키, Redirect

#### ResponseHeaderServlet

* `hello.servlet.response.ResponseHeaderServelt`

```java
package hello.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * http://localhost:8080/response-header
 */
@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // [status-line]
        response.setStatus(HttpServletResponse.SC_OK);

        // [response-headers]
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("my-header", "hello");

        // [Header 편의 메서드]
        content(response);
        cookie(response);
        redirect(response);

        // [message body]
        PrintWriter writer = response.getWriter();
        writer.println("ok");
    }

    // Content 편의 메서드
    private void content(HttpServletResponse response) {
        // Content-Type: text/plain;charset=utf-8
        // Content-Length: 2
        // response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        // response.setContentLength(2); // (생략시 자동 생성)
    }

    // 쿠키 편의 메서드
    private void cookie(HttpServletResponse response) {
        // Set-Cookie: myCookie=good; Max-Age=600;
        // response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600);
        response.addCookie(cookie);
    }

    // redirect 편의 메서드
    private void redirect(HttpServletResponse response) throws IOException {
        // Status Code 302
        // Location: /basic/hello-form.html

        // response.setStatus(HttpServletResponse.SC_FOUND); // 302
        // response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }
}

```

* `set`으로 시작되는 메소드로 원하는 메시지 요소를 직접 작성하여 `HttpResponse`를 만들 수 있다.
* 주석 처리 된 부분은 `setHeader`에서 원하는 키의 value값을 지정하는 형태이며, 주석처리가 되지 않는 부분은 `set`으로 시작하는 메소드 명으로 직접 값을 지정하는 방법이다.
    * 주로 주석처리를 하지 않는 방법을 많이 사용한다. 결과적으로는 같은 결과를 보여준다.

### 2-11. HTTP 응답 데이터 - 단순 텍스트, HTML

* HTTP 응답 메시지는 주로 다음 내용을 담아서 전달한다.
    * 단순 텍스트 응답
        * `writer.println("ok");`
    * HTML 응답
    * HTML API - MessageBody JSON 응답

#### ResponseHtmlServlet

* HttpServeltResponse - HTML 응답

```java
package hello.servlet.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: text/html;charset-utf-8
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println("    <div>안녕?</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}

```

* `response`의 `content-Type, charset`은 항상 설정하자.
    * 디폴트 값이 존재하지만 `charset`의 경우 `utf-8`이 아닌 `ISO-8859-1`이다. 이는 한글이 깨진 상태로 출력이 된다.

* `response.getWriter()`메소드를 이용해서 단순 텍스트를 HTML 형식으로 작성하여 message body를 작성했다.

### 2-12. HTTP 응답 데이터 - API JSON

#### ResponseJsonServlet

* `hello.wervlet.web.response.ResponseJsonServlet`

```java
package hello.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.servlet.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("kim");
        helloData.setAge(20);

        // {"username": "kim", "age": 20}
        String result = objectMapper.writeValueAsString(helloData);
        System.out.println(result);
        response.getWriter().write(result);
    }
}

```

* HTTP 응답으로 JSON을 반환할 때는 `content-Type`을 `application/json`으로 지정해야 한다.
    * Jackson 라이브러라가 제공하는 `objectMapper.writeValueAsString()`를 사용하면 객체를 JSON 문자로 변경할 수 있다.
    * 복습겸 언급하자면 JSON 파일 형식을 객체로 변환하는 메소드는 `readValue()`였다.
    * 메소드 명은 객체의 입장에서 JSON을 읽어서 객체로 변환해야 한. `readValue()`, 객체를 JSON형태로 작성해야 하니 `writeValue()`임을 생각하자. (역시 자바는 객체 지향)

> 참고  
> `application/json`은 스펙상 utf-8 형식을 사용하도록 정의되어 있다. 그래서 스펙에서 charset=utf-8과 같은 추가 파라미터를 지원하지 않는다. 따라서 `application/json`이라고만 사용해야지 `application/json;charset=utf-8`이라고 전달하는 것은 의미 없는 파라미터를 추가한 것이 된다.  
> `response.getWriter()`를 사용하면 추가 파라미터를 자동으로 추가해버린다. 이때는 `response.getOutputStream()`으로 출력하면 그런 문제가 없다.

## 3. 서블릿, JSP, MVC 패턴

* 동일한 요구사상의 웹 어플리케이션을 서블릿 -> JSP -> MVC 패턴으로 개발하여 이전 단계의 단점을 보완한 다음단계를 직접 학습해 본다.

#### 3-1. 회원 관리 웹 애플리케이션 요구사항

* 회원 정보
    * 이름: `username`
    * 나이: `age`

* 기능 요구사항
    * 회원 저장
    * 회원 목록 조회

##### Member

* 회원 도메인 모델
* `src/main/java/hello/servlet/domain/member/Member`

```java
package hello.servlet.domain.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {

    private Long id;
    private String username;
    private int age;

    public Member() {
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}

```

* `id`는 `Member`를 회원 저장소에 저장하면 회우너 저장소가 할당한다.

##### MemberRepository

* 회원 저장소
* `src/main/java/hello/servlet/domain/member/MemberRepository`

```java
package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong  사용 고려
 */
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance() {
        return instance;
    }

    private MemberRepository() {

    }

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}

```

* 스프링의 도움을 받지 않고 싱글톤 구현(스프링 없이 순수 서블릿으로 구현)
    * `MemberRepository`의 필드에 존재하는 `store`, `sequence`는 애플리케이션이 구동하는 동안 단 한개만 존재해야 한다.
    * `private static final MemberRepository instance = new MemberRepository()`
        * 클래스는 자신을 생성하여 그 객체(`instance`)를 가지고 있다.
    * `private MemberRepository()`
        * 생성자를 private으로 막아 버려서 함부로 객체를 생성할 수 없다.
    * `getInstance()`
        * 클래스가 생성한 객체를 반환한다.
        * 생성자 대신에 객체를 호출할 때 쓰이며, `getInstance()`로 호출된 모든 객체는 동일 객체로서 싱글톤 구현이 된다.

* 싱글톤으로 구현했기 때문에 필드를 `static`으로 선언할 필요는 없다.

##### MemberRepositoryTest

* 회원 저장소 테스트
* `src/test/java/hello/servlet/domain/member/MemberRepositoryTest`

```java
package hello.servlet.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryTest {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @After
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        // given
        Member member = new Member("hello", 20);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void findAll() {
        // given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }

}
```

* 회원을 저장하고, 목록을 조회하는 테스트 작성
    * 각 테스트가 끝날 때, 다음 테스트에 영향을 주지 않도록 각 테스트의 저장소를 `clearStore()`를 호출해서 초기화했다.
* `MemberRepository`는 싱글톤으로 구현했기 때문에 일반적인 생성자가 아닌 `getInstance`로 객체를 호출하여 사용한다.
* `assertThat(result).conatains(member1, member2)`
    * `result`에 `member1`, `member2`가 모두 포함되어 있는지를 알려준다.
    * `contains()`의 파라미터는 `List`이기 때문에 파라미터의 갯수제한은 없다.

### 3-2. 서블릿으로 회원 관리 웹 애플리케이션 만들기

#### MemberFormServlet

* 회원 등록 HTML 폼
* `src/main/java/hello/servlet/web/servlet/MemberFormServlet.java`

```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.MemberRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter w = response.getWriter();
        w.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" + " <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"/servlet/members/save\" method=\"post\">\n" +
                " username: <input type=\"text\" name=\"username\" />\n" +
                " age: <input type=\"text\" name=\"age\" />\n" +
                " <button type=\"submit\">전송</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n");
    }
}

```

* `response.getWriter()`와 `w.write()` 메소드를 이용해서 응답 메세지를 html 폼으로 작성
    * 자바로 html 문서를 작성하는 노가다 작업이 필요하다.(문화 충격...)
    * `username`과 `age`를 입력 받아서 `servlet/members/save`에 `POST`메소드의 html폼을 전송한다.
    * 코드를 보면 `response`에 대한 content-Type, encoding방식을 `HTTPServletResponse`의 메소드로 선언을 했지만 html문서에서 다시 한번 인코딩 방식을 선언하는
      중복이 발생 (spring MVC 단계에서는 이러한 과정이 전부 생략될것 같다.)

* 여기까지의 단계에서는 HTML Form 데이터를 POST로 전송해도, 전달 받는 서블릿을 아직 만들지 않았다.(`/servlet/members/save`) 그래서 오류가 발생하는 것이 정상이다.

* HTML tag 설명
    * `<form action>`
        * `<form>`태크의 `action`속성은 폼 데이터를 서버로 보낼 때 해당 데이터가 도착할 URL을 명시한다.
    * `<input>`
        * 입력 요소
        * 사용자의 데이터를 받을 수 있는 대화형 컨트롤 생성
    * `<button>`
        * 클릭이 가능한 버튼
    * 컨트롤러가 생성되는 것들은 css로 외형 변경이 가능하다.

#### MemberSaveServlet

* 회원 저장
* HTML Form에서 데이터를 입력하고 전송을 누르면 실제 회원 데이터가 저장한다.
* `src/main/java/hello/servlet/web/servlet/MemberSaveServlet.java`

```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MemberSaveServlet.service");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter w = response.getWriter();
        w.write("<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                " <li>id=" + member.getId() + "</li>\n" +
                " <li>username=" + member.getUsername() + "</li>\n" +
                " <li>age=" + member.getAge() + "</li>\n" +
                "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" +
                "</body>\n" +
                "</html>");
    }
}
```

* 파라미터를 조회해서 Member 객체를 만든다.
    * `request`에는 요청받은 `Member`에 대한 정보(`username`, `age`)가 들어있다.
    * 즉 위에서 보낸 HTML Form 에서 입력한 `username`과 `age`가 이곳으로 전달된다.

* `Member`객체를 `MemberRepository`를 통해서 저장한다.
    * 이전에도 언급했지만, 단순 text, 파라미터 형식 모두 `getParameter()`메소드로 받을 수 있다.
    * 단 모든 정보를 String으로 받기 때문에 올바른 자료형으로 변환하는 작업이 필요하다.

* `Member`객체를 사용해서 결과 화면용 HTML을 동적으로 만들어서 응답한다.
    * 자바로 HTML를 작성하기 때문에 중간중간 내가 원하는 값으로 출력하는 것이 가능하다.
    * 하지만 노가다이다.

* HTML tag
    * `<ul>`
        * Unordered List - 정돈되지 않은 리스트
        * `<ul>`태그 안에 <li> 태그를 사용하여 각 항목을 표시한다.
        * 각 항목 앞에 작은 원이나 사각형 같은 불릿(bullet)이 붙는다.(작은 원이 디폴트)
    * `<a href>`
        * html/css문서를 연결하는 a 태그
        * 다른 웹 페이지로 이동도 가능하다.

#### MemberListServlet

* 회원 목록
* 저장된 모든 회원 목록을 조회한다.
* `src/main/java/hello/servlet/web/servlet/MemberListServlet.java`

```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter w = response.getWriter();
        w.write("<html>");
        w.write("<head>");
        w.write(" <meta charset=\"UTF-8\">");
        w.write(" <title>Title</title>");
        w.write("</head>");
        w.write("<body>");
        w.write("<a href=\"/index.html\">메인</a>");
        w.write("<table>");
        w.write(" <thead>");
        w.write(" <th>id</th>");
        w.write(" <th>username</th>");
        w.write(" <th>age</th>");
        w.write(" </thead>");
        w.write(" <tbody>");

        // w.write(" <tr>");
        // w.write(" <td>1</td>");
        // w.write(" <td>userA</td>");
        // w.write(" <td>10</td>");
        // w.write(" </tr>");

        for (Member member : members) {
            w.write(" <tr>");
            w.write(" <td>" + member.getId() + "</td>");
            w.write(" <td>" + member.getUsername() + "</td>");
            w.write(" <td>" + member.getAge() + "</td>");
            w.write(" </tr>");
        }
        w.write(" </tbody>");
        w.write("</table>");
        w.write("</body>");
        w.write("</html>");
    }
}

```

* `memberRepository.findall()`을 통해 모든 회원을 조회한다.
* 저장된 회원 목록을 확인한다.
    * 자바로 html를 작성하기 때문에 for문으로 List 형태로 존재하는 `members`의 모든 `member`들을 조회한다.

* HTML tag
    * `<table>`
        * 표를 만드는 태그(웹사이트의 프레임을 잡들 때도 사용)
        * 최근에는 권장하지 않는 방식
        * `<td>` table data: 표 각각의 실제 데이터
        * `<tr>` table row: td태그를 행으로 묶어준다.
        * `<th>` table header: 테이블에서 제목이 되는 header cell을 정의(속성)
        * `<thead>` table head: 테이블 제목
        * `<tbody>` table body: 테이블 내용
        * `<tfoot>` table foot: 끝에 오는 내용

#### 템플릿 엔진으로

지금까지 서블릿과 자바 코드만으로 HTML을 만들어 보았다. 서블릿 덕분에 동적으로 원하는 HTML을 마음껏 만들 수 있다. 정적인 HTML 문서라면 화면이 계속 달라지는 회원의 저장 결과라던가, 회원 목록같은
동적인 HTML을 만드는 일은 불가능 할 것이다.       
그런데, 코드에서 보듯이 이것은 매우 복잡하고 비효율 적이다. 자바 코드로 HTML을 만들어 내는 것 보다 차라리 HTML 문서에 동적으로 변경해야 하는 부분만 자바 코드를 넣을 수 있다면 더 편리할
것이다.        
이것이 바로 템플릿 엔진이 나온 이유이다. 템플릿 엔진을 사용하면 HTML 문서에서 필요한 곳만 코드를 적용해서 동적으로 변경할 수 있다.   
템플릿 엔진에는 JSP, **Thymeleaf**, Freemarker, Velocity등이 있다.

> 참고    
> JSP는 성능과 기능면에서 다른 템플릿 엔진과의 경쟁에서 밀리면서, 점점 사장되어 가는 추세이다. 템플릿 엔진들은 각각 장단점이 있는데, 강의에서는 JSP는 앞부분에서 잠깐 다루고, 스프링과 잘 통합되는 Thymeleaf를 사용한다.

#### index.html - 변경

* 서블릿에서 JSP, MVC 패턴, 직접 만드는 MVC 프레임워크, 그리고 스프링까지 긴 여정을 함께할 페이지이다.

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
    <li>서블릿
        <ul>
            <li><a href="/servlet/members/new-form">회원가입</a></li>
            <li><a href="/servlet/members">회원목록</a></li>
        </ul>
    </li>
    <li>JSP
        <ul>
            <li><a href="/jsp/members/new-form.jsp">회원가입</a></li>
            <li><a href="/jsp/members.jsp">회원목록</a></li>
        </ul>
    </li>
    <li>서블릿 MVC
        <ul>
            <li><a href="/servlet-mvc/members/new-form">회원가입</a></li>
            <li><a href="/servlet-mvc/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v1
        <ul>
            <li><a href="/front-controller/v1/members/new-form">회원가입</a></li>
            <li><a href="/front-controller/v1/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v2
        <ul>
            <li><a href="/front-controller/v2/members/new-form">회원가입</a></li>
            <li><a href="/front-controller/v2/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v3
        <ul>
            <li><a href="/front-controller/v3/members/new-form">회원가입</a></li>
            <li><a href="/front-controller/v3/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v4
        <ul>
            <li><a href="/front-controller/v4/members/new-form">회원가입</a></li>
            <li><a href="/front-controller/v4/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v5 - v3
        <ul>
            <li><a href="/front-controller/v5/v3/members/new-form">회원가입</a></
            li>
            <li><a href="/front-controller/v5/v3/members">회원목록</a></li>
        </ul>
    </li>
    <li>FrontController - v5 - v4
        <ul>
            <li><a href="/front-controller/v5/v4/members/new-form">회원가입</a></
            li>
            <li><a href="/front-controller/v5/v4/members">회원목록</a></li>
        </ul>
    </li>
    <li>SpringMVC - v1
        <ul>
            <li><a href="/springmvc/v1/members/new-form">회원가입</a></li>
            <li><a href="/springmvc/v1/members">회원목록</a></li>
        </ul>
    </li>
    <li>SpringMVC - v2
        <ul>
            <li><a href="/springmvc/v2/members/new-form">회원가입</a></li>
            <li><a href="/springmvc/v2/members">회원목록</a></li>
        </ul>
    </li>
    <li>SpringMVC - v3
        <ul>
            <li><a href="/springmvc/v3/members/new-form">회원가입</a></li>
            <li><a href="/springmvc/v3/members">회원목록</a></li>
        </ul>
    </li>
</ul>
</body>
</html>
```

### 3-3. JSP로 회원 관리 웹 애플리케이션 만들기

#### JSP 라이브러리 추가

* `build.gradle`에 추가(`dependencies`항목에 추가함)

```
implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
implementation 'javax.servlet:jstl'
```

#### new-form.jsp

* `src/main/webapp/jsp/members/new-form.jsp`

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
    username: <input type="text" name="username"/>
    age: <input type="text" name="age"/>
    <button type="submit">전송</button>

</body>
</html>
```

* `<%@ page contentType="text/html;charset=UTF-8" language="java" %>`
    * 첫 줄은 JSP문서라는 뜻이다. JSP 문서는 이렇게 시작해야 한다.

* 회원등록 폼 JSP를 보면 첫 줄을 제외하고는 완전히 HTML와 똑같다. JSP는 서버 내부에서 서블릿으로 변환되는데, 우리가 만들었던 `MemberFormServlet`과 거의 비슷한 모습으로 변환된다.

* 참고로 IntelliJ community 버전은 jsp 파일 지원이 안된다.(작성만 가능) 따라서 메모장에 코딩하는 것과 다를것이 없는 느낌이다.

#### save.jsp

* `src/main/webapp/jsp/members/save.jsp`

```
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // request, response 사용 가능
    MemberRepository memberRepository = MemberRepository.getInstance();
    
    System.out.println("save.jsp");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));
    
    Member member = new Member(username, age);
    System.out.println("member = " + member);
    memberRepository.save(member);
%>
<html>
<head>
    <meta charset="UTF-8">
</head>
<body>성공
<ul>
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

* JSP는 자바 코드를 그대로 다 사용할 수 있다.
* `<%@ page import="hello.servlet.domain.member.MemberRepository" %>`
    * 자바의 import문과 같다.

* `<% ~ %>`
    * 이 부분에는 자바 코드를 입력할 수 있다.

* `<%= ~ %>`
    * 이 부분에는 자바 코드를 출력할 수 있다.

* 회원 저장 JSP를 보면, 회원 저장 서블릿 코드와 같다. 다른 점이 있다면, HTML을 중심으로 하고, 자바 코드를 부분부분 입력해주었다. `<% ~ %>`를 사용해서 HTML 중간에 자바 코드를 출력하고
  있다.

#### members.jsp

* `src/main/webapp/jsp/members.jsp`

```
<%@ page import="java.util.List" %>
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
     MemberRepository memberRepository = MemberRepository.getInstance();
     List<Member> members = memberRepository.findAll();
%>
<html>
<head>
     <meta charset="UTF-8">
     <title>Title</title>
</head><body>
<a href="/index.html">메인</a>
<table>
 <thead>
     <th>id</th>
     <th>username</th>
     <th>age</th>
 </thead>
 <tbody>
<%
     for (Member member : members) {
     out.write(" <tr>");
     out.write(" <td>" + member.getId() + "</td>");
     out.write(" <td>" + member.getUsername() + "</td>");
     out.write(" <td>" + member.getAge() + "</td>");
     out.write(" </tr>");
 }
%>
 </tbody>
</table>
</body>
</html>
```

* 회원 리포지토리를 먼저 조회하고, 결과 List를 사용해서 중간에 `<tr><td>`HTML 태그를 반복해서 출력하고 있다.

#### 서블릿과 JSP의 한계

서블릿으로 개발 할 때는 뷰(View)화면을 위한 HTML을 만드는 작업이 자바 코드에 섞여서 지저분하고 복잡했다.    
JSP를 사용한 덕분에 뷰를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간중간 동적으로 변경이 필요한 부분에만 자바 코드를 적용했다. 그런데 이렇게 해도 해결되지 않는 몇가지 고민이 남는다.

회원 저장 JSP를 보자. 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 나머지 하위 절반만 결과를 HTML로 보여주기 위한 뷰 영역이다. 회원 목록의 경우에도 마찬가지다.

코드를 잘 보면 JAVA코드, 데이터를 조회하는 리포지토리 등등 다양한 코드가 모두 JSP에 노출되어 있다. JSP가 너무 많은 역할을 한다. 이렇게 작은 프로젝트도 벌써 머리가 아파오는데, 수백 수천줄이 넘어가는
JSP를 떠올려보면 정말 지옥과 같을 것이다.

#### MVC 패턴의 등장

비즈니스 로직은 서블릿 처럼 다른곳에서 처리하고, JSP는 목적에 맞게 HTML로 화면(View)을 그리는 일에 집중하도록 하자. 과거 개발자들도 모두 비슷한 고민이 있었고, 그래서 MVC 패턴이 등장했다. 우리도
직접 MVC 패턴을 적용해서 프로젝트를 리팩터링 해보자.

### 3-4. MVC 패턴 - 개요

#### 너무 많은 역할

하나의 서블릿이나 JSP만으로 비즈니스 로직과 뷰 렌더링까지 모두 처리하게 되면, 너무 많은 역할을 하게되고, 결과적으로 유지보수가 어려워진다. 비즈니스 로직을 호출하는 부분에 변경이 발생해도 해당 코드를 손대야
하고, UI를 변경할 일이 있어도 비즈니스 로직이 함께 있는 해당 파일을 수정해야 한다.       
HTML코드 하나 수정해야 하는데, 수백줄의 자바 코드가 함께 있다고 상상해보라! 또는 비즈니스 로직을 하나 수정해야 하는데 수백 수천줄을 HTML 코드가 함께 있다고 상상해보라.

#### 변경의 라이프 사이클

사실 이게 정말 중요한데, 진짜 문제는 둘 사이에 변경의 라이프 사이클이 다르다는 점이다. 예를 들어서 UI를 일부 수정하는 일과 비즈니스 로직을 수정하는 일은 각각 다르게 발생할 가능성이 매우 높고 대부분 서로에게
영향을 주지 않는다. 이러한 변경의 라이프 사이클이 다른 부분을 하나의 코드로 관리하는 것은 유지보수하기 좋지 않다. (물론 UI가 많이 변하면 함께 변경될 가능성도 있다.)

#### 기능 특화

특히 JSP 같은 뷰 템플릿은 화면을 렌더링 하는데 최적화 되어 있기 때문에 이 부분의 업무만 담당하는 것이 가장 효과적이다.

#### Model View Controller

MVC 패턴은 지금까지 학습한 것 처럼 하나의 서블릿이나, JSP로 처리하던 것을 컨트롤러(Controller)와 뷰(View)라는 영역으로 서로 역할을 나눈 것을 말한다. 웹 애플리케이션은 보통 이 MVC 패턴을
사용한다.

* 컨트롤러: HTTP 요청을 받아서 파라미터를 검증하고, 비즈니스 로직을 실행한다. 그리고 뷰에 전달할 결과 데이터를 조회해서 모델에 담는다.
* 모델: 뷰에 출력할 데이터를 담아둔다. 뷰가 필요한 데이터를 모두 모델에 담아서 전달해주는 덕분에 뷰는 비즈니스 로직이나 데이터 접근을 몰라도 되고, 화면을 렌더링 하는 일에 집중할 수 있다.
* 뷰: 모델에 담겨있는 데이터를 사용해서 화면을 그리는 일에 집중한다. 여기서는 HTML을 생성하는 부분을 말한다.

> 참고    
> 컨트롤러에 비즈니스 로직을 둘 수도 있지만, 이렇게 되면 컨트롤러가 너무 많은 역할을 담당한다. 그래서 일반적으로 비즈니스 로직은 서비스(Service)라는 계층을 별도로 만들어서 처리한다. 그리고 컨트롤러는 비즈니스 로직이 있는 서비스를 호출하는 것을 담당한다. 참고로 비즈니스 로직을 변경하면 비즈니스 로직을 호출하는 컨트롤러의 코드도 변경될 수 있다. 앞에서는 이해를 돕기 위해 비즈니스 로직을 호출한다는 표현보다는, 비즈니스 로직이라 설명했다.

##### MVC 패턴 이전

![](https://i.ibb.co/CwgpMFr/bandicam-2021-06-05-16-15-39-935.jpg)

##### MVC 패턴 1

![](https://i.ibb.co/Fmpb9k1/bandicam-2021-06-05-16-17-01-565.jpg)

##### MVC 패턴 2

![](https://i.ibb.co/W2MjbHn/bandicam-2021-06-05-16-18-06-544.jpg)

### 3-5. MVC 패턴 - 적용

서블릿을 컨트롤러로 사용하고 JSP를 뷰로 사용해서 MVC 패턴을 적용한다.  
Model은 `HttpServletRequest`객체를 사용한다. `request`는 내부에 데이터 저장소를 가지고 있는데, `request.setAttribute()`, `request.getAttribute()`
를 사용하면 데이터를 보관하고, 조회할 수 있다.

#### MvcMemberFormServlet

* 회원 등록 폼 - 컨트롤러
* `src/main/java/hello/servlet/web/servletmvc/MvcMemberFormServlet.java`

```java
package hello.servlet.web.servletmvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* `dispatcher.forward()`
    * 다른 서블릿이나 JSP로 이동할 수 있는 기능이다.
    * 서버 내부에서 다시 호출이 발생한다.
    * view의 경로를 통해 JSP로 넘어간다. (controller -> view)

* 등록 화면을 보여주는 단계이므로 특별한 비즈니스 로직은 존재하지 않는다. 그럼에도 controller와 view의 확실한 분리를 위해서 작성된 케이스 이다.

> /WEB-INF  
> 이 경로안에 JSP가 있으며 외부에서 직접 JSP를 호출할 수 없다. 우리가 기대하는 것은 항상 컨트롤러를 통해서 JSP를 호출하는 것이다.

> redirect vs forward   
> 리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시 요청한다. 따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다. 반면에 포워드는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.

#### new-form.jsp

* 회원 등록 폼 - 뷰
* `src/main/webapp/jsp/members/new-form.jsp`

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
     <meta charset="UTF-8">
     <title>Title</title>
</head>
<body>
<!-- 상대경로 사용, [현재 URL이 속한 계층 경로 + /save] -->
<form action="save" method="post">
     username: <input type="text" name="username" />
     age: <input type="text" name="age" />
     <button type="submit">전송</button>
</form>
</body>
</html>
```

* `<form`의 `action`을 보면 절대 경로가 아니라 상대경로로 작성된 것을 확인할 수 있다.
    * 폼 전송시 현재 URL이 속한 계층 경로 + save가 호출된다.
    * 현재 계층 경로: `/servlet-mvc/members/`
    * 결과: `/servlet-mvc/members/save`

#### MvcMemberSaveServlet

* 회원 저장 - 컨트롤러
* `src/main/java/hello/servlet/web/servletmvc/MvcMemberSaveServlet.java`

```java
package hello.servlet.web.servletmvc;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        // Model에 데이터 보관
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* `HttpServletRequest`를 Model로 사용한다.
    * `request`가제공하는 `setAttribute()`를 사용하면 `request`객체에 데이터를 보관해서 view에 전달할 수 있다.
    * view는 `request.getAttribute()`를 사용해서 데이터를 꺼내면 된다.

#### save-result.jsp

* 회원 저장 - 뷰
* `src/main/webapp/WEB-INF/views/save-result.jsp`

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
 <meta charset="UTF-8">
</head>
<body>
성공
<ul>
     <li>id=${member.id}</li>
     <li>username=${member.username}</li>
     <li>age=${member.age}</li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

* `<%= request.getAttrubute("member")%>`로 모델에 저장한 `member`객체를 꺼낼 수 있지만, 너무 복잡해진다.
* JSP는 `${}`문법을 제공하는데, 이 문법을 사용하면 request의 attribute에 담긴 데이터를 편리하게 조회할 수 있다.

#### MvcMemberListServlet

* 회원 목록 조회 - 컨트롤러
* `src/main/java/hello/servlet/web/servletmvc/MvcMemberListServlet.java`

```java
package hello.servlet.web.servletmvc;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "mvcMemberListServlet", urlPatterns = "/servlet-mvc/members")
public class MvcMemberListServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* request 객체를 사용해서 `List<Member> members`를 모델에 보관

#### members.jsp

* 회원 목록 조회 - 뷰
* `src/main/webapp/WEB-INF/views/members.jsp`

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
 <meta charset="UTF-8">
 <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
     <thead>
     <th>id</th>
     <th>username</th>
     <th>age</th>
     </thead>
     <tbody>
         <c:forEach var="item" items="${members}">
         <tr>
             <td>${item.id}</td>
             <td>${item.username}</td>
             <td>${item.age}</td>
         </tr>
         </c:forEach>
     </tbody>
</table>
</body>
</html>
```

* 모델에 담아둔 members를 JSP가 제공하는 taglib기능을 사용해서 반복하면서 출력
    * `members`리스트에서 `member`를 순서대로 꺼내서 `item`변수에 담고, 출력하는 과정을 반복한다.
    
* `<c:forEach>` 기능을 사용하려면 다음과 같이 선언해야 한다.
        * `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>`
  

# Note

* IntelliJ 무료버전일때 `War`의 경우 톰캣이 정상 시작되지 않는 경우가 생김
    * 이땐 build를 gradle로 다시 설정하거나
    * `provideRuntime 'org.springframework.boot:spring-boot-starter-tomcat`을 제거해야 한다.