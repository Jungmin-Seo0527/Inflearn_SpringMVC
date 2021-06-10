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

* `<c:forEach>` 기능을 사용하려면 다음과 같이 선언해야 한다. * `<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>`

### 3-6. MVC 패턴 - 한계

MVC 패턴을 적용한 덕분에 컨트롤러의 역할과 뷰를 렌더링 하는 역할을 명확하게 구분할 수 있다. 특히 뷰는 화면을 그리는 역할에 충실한 덕분에, 코드가 깔끔하고 직관적이다. 단순하게 모델에서 필요한 데이터를
꺼내고, 화면을 만들면 된다.   
그런데 컨트롤러는 딱 봐도 중복이 많고, 필요하지 않는 코드들도 많이 보인다.

#### MVC 컨트롤러의 단점

##### 포워드 중복

View로 이동하는 코드가 항상 중복 호출되어야 한다. 물론 이 부분을 메서드로 공통화해도 되지만, 해당 메서드도 항상 직접 호출해야 한다.

```
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

##### ViewPath에 중복

```
String viewPath = "/WEB-INF/views/new-form.jsp";
```

* prefix: `/WEB-INF/views`
* suffix: `.jsp`
  그리고 만약 jsp가 아닌 thymeleaf 같은 다른 뷰로 변경한다면 전체 코드를 다 변경해야 한다.

##### 사용하지 않는 코드

다음 코드를 사용할 때도 있고, 사용하지 않을 때도 있다. 특히 response는 현재 코드에서 사용되지 않는다.

```
HttpServletRequest request, HttpServletResponse response
```

그리고 이런 `HttpServletRequest`, `HttpServletResponse`를 사용하는 코드는 테스트 케이스를 작성하기도 어렵다.

##### 공통 처리가 어렵다.

기능이 복잡해질 수록 컨트롤러에서 공통으로 처리해야 하는 부분이 점점 더 많이 증가할 것이다. 단순히 공통 기능을 메서드로 뽑으면 될 것 같지만, 결과적으로 해당 메서드를 항상 호출해야 하고, 실수로 호출하지 않으면
문제가 될 것이다. 그리고 호출하는 것 자체도 중복이다.

##### 정리하면 공통 처리가 어렵다는 문제가 있다.

이 문제를 해결하려면 컨트롤러 호출 전에 먼저 공통 기능을 처리해야 한다. 소위 **수문장 역할**을 하는 기능이 필요하다. **프론트 컨트롤러(Front Controller)패턴**을 도입하면 이런 문제를 깔끔하게
해결할 수 있다. (입구를 하나로!!!)     
스프링 MVC의 핵심도 바로 이 프론트 컨트롤러에 있다.

## 4. MVC 프레임워크 만들기

### 4-1. 프론트 컨트롤러 패턴 소개

* 프론트 컨트롤러 도입 전
  ![](https://i.ibb.co/MDZJhR6/bandicam-2021-06-07-18-05-28-379.jpg)

* 프론트 컨트롤러 도입 후
  ![](https://i.ibb.co/61qfV5v/bandicam-2021-06-07-18-05-37-093.jpg)

* FrontController 패턴 특징
    * 프론트 컨트롤러 서블릿 하나로 클라이언트의 요청을 받음
    * 프론트 컨트롤러가 요청에 맞는 컨트롤러를 찾아서 호출
    * 입구를 하나로!!!
    * 공통 처리 가능
    * 프론트 컨트롤러를 제외한 나머지 컨트롤러는 서블릿을 사용하지 않아도 됨

* 스프링 웹 MVC와 프론트 컨트롤러
    * 스프링 웹 MVC의 핵심이 바로 **FrontController**
    * 스프링 웹 MVC의 **DispatcherServlet**이 FrontController 패턴으로 구현되어 있음

### 4-2. 프론트 컨트롤러 도입 - v1

프론트 컨트롤러를 단계적으로 도입해보자.  
이번 목표는 기존 코드를 최대한 유지하면서, 프론트 컨트롤러를 도입하는 것이다. 먼저 구조를 맞추어두고 점진적으로 리펙터링 해보자.

#### v1 구조

![](https://i.ibb.co/rx1fW0z/bandicam-2021-06-07-19-17-07-773.jpg)

#### ControllerV1

```java
package hello.servlet.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV1 {

    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

```

서블릿과 비슷한 모양의 컨트롤러 인터페이스를 도입한다. 각 컨트롤러들은 이 인터페이스를 구현하면 된다. 프론트 컨트롤러는 이 인터페이스를 호출해서 구현과 관계없이 로직의 일관석을 가져갈 수 있다.

이제 이 인테페이스를 구현한 컨트롤러를 만들어보자. 지금 단계에서는 기존 로직을 최대한 유지하는게 핵심이다.

#### MemberFormControllerV1

* 회원 등록 컨트롤러

```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerV1 implements ControllerV1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* `HttpServlet`를 상속받는 것이 아닌 인터페이스 `ControllerV1`을 구현하였다.
    * 이후 다형성을 이용하여 매핑을 하기 위함
    * 단 `@WebServlet`를 사용하지 않으며, URI를 통한 매핑이 되지 않는다.
* 서블릿 부분(Controller)과 완전히 동일한 코드라고 해도 무방하다.
    * 각각의 컨트롤러들은 동일하게 만든다.(현재 단계에서는...)

#### MemberSaveControllerV1

* 회원 저장 컨트롤러

```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

#### MemberListControllerV1

* 회원 목록 컨트롤러

```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* 전체적으로 내부 로직은 기존 서블릿과 거의 같다.

#### FrontControllerServletV1

* 프론트 컨트롤러

```java
package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {

    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    public FrontControllerServletV1() {
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV1.service");

        String requestURI = request.getRequestURI();

        ControllerV1 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        controller.process(request, response);
    }
}

```

* `urlPatterns`
    * `urlPatterns = "/front-controller/v1/*"`: `/front-controller/v1`를 포함한 하위 모든 요청은 이 서블릿에서 받아들인다.
    * 예) `/front-controller/v1`, `/front-controller/v1/a`, `/front-controller/v1/a/b`

* `controllerMap`
    * key: 매핑 URL
    * value: 호출될 컨트롤러

* `servie()`
    * 먼저 `requestURI`를 조회해서 실제 호출할 컨트롤러를 `controllerMap`에서 찾는다.
        * 만약 없으면 404(`SC_NOT_FOUND`)상태 코드를 반환
    * 컨트롤러를 찾고 `controller.process(request, response);`을 호출해서 해당 컨트롤러를 실행한다.

* JSP
    * JSP는 이전 MVC에서 사용했던 것을 그대로 사용한다.

* 이전의 sevletMVC는 각 컨트롤러에 url패턴을 지정하였다면, 이번에는 모든 url 패턴이 `FrontControllerServletV1`으로 우선 간 후에 그 안에서 url를 분석하여 해당 컨트롤러로 다시
  뿌려진다.
    * 아직까지는 더 좋다는 느낌은 없다.
* 단 인터페이스의 구현체로 컨트롤러를 구현하고, 각 구현체들을 Map에 저장하였다. 이후에 Map에서 꺼낼때는 `ControllerV1 controller = controllerMap.get(requestURI)`
  형식으로 받았다.
    * 인터페이스와 구현체를 분리시고, map에서 받은 구현체들은 다시 인터페이스 형식으로 받았기에 어떠한 구현체가 호출되어도 단 한줄로 모두 받아낼 수 있다.(위대한 부모...) 이러한 방식으로 코딩하는 것은
      눈여겨 볼 필요가 있으며 실무에서도 많이 쓰이는 방식이라고 한다. (**다형성!!!!**)

### 4-3. View 분리 - v2

모든 컨트롤러에서 뷰로 이동하는 부분에 중복이 있고, 깔끔하지 않다.

```
String viewPath = "/WEB-INF/wiews/new-form.jsp"
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

이 부분을 깔끔하게 분리하기 위해 별도로 뷰를 처리하는 객체를 만들자.

#### V2 구조

![](https://i.ibb.co/6tsstqd/bandicam-2021-06-07-21-38-20-244.jpg)

#### MyView

뷰 객체는 이후 다른 버전에서도 함께 사용하므로 패키지 위치를 `frontController`에 두었다.

```java
package hello.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyView {

    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

```

* 생성자에서 `viewPath`를 파라미터로 받는다.

```
String viewPath = "/WEB-INF/wiews/new-form.jsp"
```  

* 이후 `render`에서 랜더링 작업을 수행한다.

```
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

* 중복되는 코드를 각각 생성자와 `render`메소드로 나누어서 작성했다고 이해하면 된다.

#### ControllerV2

```java
package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV2 {

    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

```

* 이전의 `ControllerV1`의 `process()`는 `void`를 반환한 것과는 다르게 `ControllerV2`의 `process()`는 `MyView`를 반환하고 있다.
    * 그 이유는 `ControllerV2`의 구현체와 `FrontControllerV2`에서 확인할 수 있다.

#### MemberFormControllerV2

* 회원 등록 폼

```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerV2 implements ControllerV2 {

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}

```

* 각 컨트롤러는 복잡한 `dispatcher.forward()`를 직접 생성해서 호출하지 않아도 된다.
    * 단순히 `MyView`객체를 생성하고 거기에 뷰 이름만 넣고 반환하면 된다.

* `ControllerV1`를 구현한 클래스와 `ControllerV2`를 구현한 클래스를 비교해보면, 이 부분의 중복이 확실하게 제거된 것을 확인할 수 있다.

#### MemberSaveControllerV2

* 회원 저장

```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV2 implements ControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        request.setAttribute("member", member);
        return new MyView("/WEB-INF/views/save-result.jsp");
    }
}

```

#### MemberListControllerV2

* 회원 목록

```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerV2 implements ControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();
        request.setAttribute("members", members);
        return new MyView("/WEB-INF/views/members.jsp");
    }
}

```

#### FrontControllerV2

```java
package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {

    private Map<String, ControllerV2> controllerMap = new HashMap<>();

    public FrontControllerServletV2() {
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV2 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyView view = controller.process(request, response);
        view.render(request, response);
    }
}
```

* ControllerV2의 반환 타입이 `Myview`이므로 프론트 컨트롤러는 컨트롤러의 호출 결과로 `MyView`를 반환 받는다. 그리고 `view.render()`를 호출하면 `forward()`로직을
  수행해서 JSP가 실행된다.

* 프론트 컨트롤러의 도입으로 `MyView`객체의 `render()`를 호출하는 부분을 모두 일관되게 처리할 수 있다. 각각의 컨트롤러는 `MyView`객체를 생성만 해서 반환하면 된다.

* 현 단계에서는 중복되는 코드를 메소드로 abstract하여 간결화 하였다.
    * 간결화 한 방법이 객체를 반환해서 객체의 메소드를 실행 하도록 한 방법은 참고할 만 하다.

### 4-4. Model 추가 - v3

#### 서블릿 종속성 제거

* 컨트롤러 입장에서 `HttpServletRequest`, `HttpServletResponse`이 꼭 필요할까?
* 요청 파라미터 정보는 자바의 `Map`으로 대신 넘기도록 하면 지금 구조에서는 컨트롤러가 서블릿 기술을 몰라도 동작할 수 있다.
* `request`객체를 `Model`로 사용하는 대신에 별도의 `Model`객체를 만들어서 반환하면 된다.
* 우리가 구현하는 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 변경해보자.
    * 구현 코드도 매우 단순해지고, 테스트 코드 작성이 쉽다.

#### 뷰 이름 중복 제거

* 컨트롤러에서 지정하는 뷰 이름에 중복이 있는 것을 확인 할 수 있다.
* 컨트롤러는 **뷰의 논리 이름**을 반환하고, 실제 물리 위치의 이름은 프론트 컨트롤러에서 처리하도록 단순화 하자.
* 이렇게 해두면 향후 뷰의 폴더 위치가 함께 이동해도 프론트 컨트롤러만 고치면 된다.
    * `/WEB-INF/views/new-form.jsp` -> **new-form**
    * `/WEB-INF/views/save-result.jst` -> **sve-result**
    * `/WEB-INF/views/members.jsp` -> **members**

#### V3 구조

![](https://i.ibb.co/6tsstqd/bandicam-2021-06-07-21-38-20-244.jpghttps://i.ibb.co/02b0P0j/bandicam-2021-06-09-16-17-36-460.jpg)

#### ModelView

지금까지 컨트롤러에서 서블릿에 종속적인 `HttpServletRequest`를 사용했다. 그리고 `Model`도 `request.setAttribute()`를 통해 데이터를 저장하고 뷰에 전달했다.  
서블릿의 종속성을 제거하기 위해 Model을 직접 만들고, 추가로 View 이름까지 전달하는 객체를 만들어 보자.     
(이번 버전에서는 컨트롤러에서 `HttpServletRequest`를 사용할 수 없다. 따라서 직접 `request.setAttribute()`를 호출할 수 도 없다. 따라서 Model이 별도로 필요하다.)

참고로 `ModelView`객체는 다른 버전에서도 사용하므로 패키지를 `frontcontroller`에 둔다.

* `src/main/java/hello/servlet/web/frontcontroller/ModelView.java`

```java
package hello.servlet.web.frontcontroller;

import java.util.HashMap;
import java.util.Map;

public class ModelView {

    private String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}

```

뷰의 이름과 뷰를 랜더링할 때 필요한 model 객체를 가지고 있다. model은 단순히 map으로 되어 있으므로 컨트롤러에서 뷰에 필요한 데이터를 key, value로 넣어주면 된다.

* `Map<String, Object> model = new HashMap<>()`
    * 만약 save를 예로 들어 보자.
    * `request`에서 `getParameter()`를 이용해서 `username`, `age`를 뽑아서 `Member`객체를 만든 후에 그 객체를 `MemberRepository`에
      저장하고, `Member`객체를 `save-result.jsp`로 넘겨서 출력을 하기 위해서 model에 넘겨주는 행위로 `request.setAttribute("member", member)`를 했다.
    * `setAttribute()`메소드의 파라미터가 객체의 이름과 실제 객체를 파라미터로 받는것에서 Map을 이용하면 Model을 구현할 수 있다는 것을 알 수 있다.

#### ControllerV3

* Controller 인터페이스
* `src/main/java/hello/servlet/web/frontcontroller/v3/ControllerV3.java`

```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {

    ModelView process(Map<String, String> paramMap);
}

```

* 이 컨트롤러는 서블릿 기술을 전혀 사용하지 않는다.
    * 따라서 구현이 매우 단순해지고, 테스트 코드 작성시 테스트 하기 쉽다.

* `HttpServletRequest`가 제공하는 파라미터는 프론트 컨트롤러가 `paramMap`에 담아서 호출해주면 된다.
* 응답 결과로 뷰 이름과 뷰에 전달할 Model 데이터를 포함하는 `ModelView`객체를 반환하면 된다.
    * 이후 `FrontController`에서 반환받는 `ModelView`에서 뷰의 이름과 모델을 바탕으로 JSP 문서롤 호출함과 동시에 JSP에서는 필요한 데이터를 model에서 꺼내서 쓸수 있다.

#### MemberFromControllerV3

* 회원 등록 폼
* `src/main/java/hello/servlet/web/frontcontroller/v3/controller/MemberFormControllerV3.java`

```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberFormControllerV3 implements ControllerV3 {

    @Override
    public ModelView process(Map<String, String> paramMap) {
        return new ModelView("new-form");
    }
}

```

* `ModelView`를 생성할 때 `new-form`이라는 view의 **논리적인 이름**을 지정한다.
    * 실제 물리적인 이름은 프론트 컨트롤러에서 처리한다.
    * 물리적인 이름: `/WEB-INF/views/new-form.jsp`
    * 모든 jsp를 같은 패키지인 `/WEB-INF/views`에 저장하니 이 부분을 중복으로 작성하는 것을 피하고자 하는 방법이다.
    * 논리적인 이름이 jsp의 파일 이름이라고 생각하면 쉽다.

#### MemberSaveControllerV3

* 회원 저장
* `src/main/java/hello/servlet/web/frontcontroller/v3/controller/MemberSaveControllerV3.java`

```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelView mv = new ModelView("save-result");
        mv.getModel().put("member", member);
        return mv;
    }
}

```

* `paramMap.get("username")`
    * 파라미터 정보는 `map`에 담겨있다. map에서 필요한 요청 파라미터를 조회하면 된다.
    * `v2`에서는 `request`에서 직접 `getParameter()`를 사용해서 파라미터 정보를 뽑아내었다. 이 부분에서 서블릿에 종속적이므로 이를 없애고자 `HttpServletRequest`객체
      대신에 객체의 정보를 `paramMap`에 전달 한 후에 이를 파라미터도 전달 받았다.
    * 역할로 보자면 `paramMap` = `HttpServletRequest`라고 생각해도 무방하다.

* `mv.getModel().put("member", member)`
    * 모델은 단순한 map이므로 모델에 뷰에서 필요한 `member`객체를 담고 반환한다.
    * `request.setAttribute("member", member)`와 동일한 과정이다.
    * 이전에 `request`를 Model로 사용했다면 이번에는 Model를 따로 구현했기 때문에 그 Model(`mv.getModel()`)에 JSP가 필요로 하는 데이터를 넘겨주는 것이다.

#### MemberListControllerV3

* 회원 목록
* `src/main/java/hello/servlet/web/frontcontroller/v3/controller/MemberListControllerV3.java`

```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);
        return mv;
    }
}

```

#### FrontControllerServletV3

* `src/main/java/hello/servlet/web/frontcontroller/v3/FrontControllerServiceV3.java`

```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServiceV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServiceV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServiceV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV3 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);

        view.render(mv.getModel(), request, response);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

```

* `view.render(mv.getModel(), request, response)`코드에 컴파일 오류가 발생할 것이다. (`MyView`클래스에서 메소드 추가가 필요하다.)

* `Map<String, String> paramMap = createParamMap(request)`
    * `HttpServletRequest`역할을 대신하는 `paramMap`을 생성한다.
    * `createParamMap()`
        * `HttpServletRequest`를 파라미터로 받아서 그 정보들을 모두 Map형태로 저장한 후에 그 Map을 반환한다. 즉 `HttpServletRequest` -> `Map`

* `ModelView mv = controller.process(paramMap)`
    * `paramMap`을 전달하여 각 컨트롤러를 실행시킨다.
    * 각 컨트롤러의 실행 결과는 `ModelView`객체로 반환되며 이 객체는 View를 위한 데이터가 저장되어 있다.
    * 복습겸 언급하자면 `ModelView`객체에는 `view`의 논리적 이름(jsp 파일 이름)과 jsp 파일이 필요로 하는 데이터가 저장되어 있는 model로 구성되어 있다.

* `viewResolver(viewName)`
    * 논리적인 이름으로 저장되어 있는 `viewName`을 물리적 경로를 추가한 물리적 이름, 즉 경로를 포함하고 있는 형태로 변환하는 것이다.
        * 논리 뷰 이름: `member`
        * 물리 뷰 경로: `/WEB-INF/views/members.jsp`
    * 모든 jsp파일은 `/WEB-INF/views`에 저장되어 있고, 모든 파일이 jsp이므로 확장자는 `.jsp`이다.
    * `MyView`객체에 파라미터로 물리적인 뷰 이름을 전달하여 생성한다.

#### MyView

* `render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)` 메소드 추가
* `src/main/java/hello/servlet/web/frontcontroller/MyView.java`

```java
package hello.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MyView {

    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    // --------------------- 추가 시작 -------------------------------
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        modelToRequestAttribute(model, request);
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    private void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach(request::setAttribute);
    }

    // --------------------- 추가 끝---------------------------------
}

```

* 뷰 객체를 통해서 HTML 화면을 렌더링 한다.
* 뷰 객체의 `render()`는 모델 정보도 함께 받는다.
* JSP는 `request.getAttribute()`로 데이터를 조회하기 때문에, 모델의 데이터를 꺼내서 `request.setAttrubute()`로 담아둔다.
* JSP로 forward 해서 JSP를 렌더링 한다.

> `MyView`에서 알수 있듯이, `ModelView`를 만들어서 `HttpServletRequest`객체가 Model의 역할을 수행했던 부분을 대신했지만, 모든 역할을 수행하진 못한다.
>   * HTML 화면을 랜더링 하는 과정에서는 결국 필요한 데이터를 `HttpServletRequest`객체에서 뽑아서 사용한다.
>   * 지금 버전은 Controller의 입장에서 서블릿에 종속하지 않도록 구현하기 위한 Model를 만든 것이다.
>   * 가짜 모델(`ModelView`)은 다시 모든 정보를 `request.setAttrubute()`를 통해 `request`에 넘겨주어야 한다.
>
> 처음에는 `ModelView`의 `model`이 Model의 모든 역할을 수행할 수 있다고 이해해서 코드 이해가 되지 않았다. 주의하자. 마지막엔 다시 모든 정보를 `request.setAttrubue()`로 전달한다.

### 4-5. 단순하고 실용적인 컨트롤러 - v4

앞서 만든 v3 컨트롤러는 서블릿 종속성을 제거하고 뷰 경로의 중복을 제거하는 등, 잘 설계된 컨트롤러이다. 그런데 실제 컨트롤러 인터페이스를 구현하는 개발자 입장에서 보면, 항상 `ModelView`객체를 생성하고
반환해야 하는 부분이 조금은 번거롭다.      
좋은 프레임워크는 아키텍처도 중요하지만, 그와 더불어 실제 개발하는 개발자가 단순하고 편리하게 사용할 수 있어야 한다. 소위 실용성이 있어야 한다.

이번에는 v3를 조금 변경해서 실제 구혀하는 개발자들이 매우 편리하게 개발할 수 있는 v4 버전을 개발해 보자.

#### v4 구조

![](https://i.ibb.co/DWYgYxb/bandicam-2021-06-09-18-02-24-217.jpg)

* 기본적인 구조는 v3와 같다. 대신에 컨트롤러가 `ModelView`를 반환하지 않고, `ViewName`만 반환한다.

#### ControllerV4

* `src/main/java/hello/servlet/web/frontcontroller/v4/ControllerV4.java`

```java
package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

public interface ControllerV4 {

    /**
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}

```

* 파라미터로 기존의 `paramMap`이외에 `model`를 추가로 받는다.
    * `ModelView` 필드의 `model`이 이번에는 파라미터로 전달된다.

* 이번버전은 이터페이스에 `ModelView`가 없다. model 객체는 파라미터로 전달되기 때문에 그냥 사용하면 되고, 결과로 뷰의 이름만 반환해주면 된다.

#### MemberFormControllerV4

* `src/main/java/hello/servlet/web/frontcontroller/v4/controller/MemberFormControllerV4.java`

```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberFormControllerV4 implements ControllerV4 {

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form";
    }
}

```

#### MemeberSaveControllerV4

* `src/main/java/hello/servlet/web/frontcontroller/v4/controller/MemberSaveControllerV4.java`

```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberSaveControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);
        model.put("member", member);
        return "save-result";
    }
}

```

* `model.put("member", member)`
    * 모델이 파라미터로 전달되기 때문에, 모델을 직접 생성하지 않아도 된다.

#### MemberListControllerV4

* `src/main/java/hello/servlet/web/frontcontroller/v4/controller/MemberListControllerV4.java`

```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.List;
import java.util.Map;

public class MemberListControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();

        model.put("members", members);
        return "members";
    }
}

```

#### FrontControllerServletV4

* `src/main/java/hello/servlet/web/frontcontroller/v4/FrontControllerServiceV4.java`

```java
package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServiceV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerServletV4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV4 controller = controllerMap.get(requestURI);
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);

        MyView view = viewResolver(viewName);
        view.render(model, request, response);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

```

* 이전버전(`FrontControllerServletV3`)과 거의 동일하다.

* 모델 객체 전달
    * Map<Stgring, Object> model = new HashMap<>();`
    * 모델 객체를 프론트 컨트롤러에서 생성해서 넘겨준다. 컨트롤러에서 모델 객체에 값을 담으면 여기에 그대로 담겨있게 된다.

* 뷰의 논리 이름을 직접 반환
    * 컨트롤러가 직접 뷰의 논리 이름을 반환하므로 이 값을 사용해서 실제 물리 뷰를 찾을 수 있다.

```
String viewName = controller.process(paramMap, model);
MyView view = viewREsolver(viewName);
```

> v3 를 확실하게 이해하고 넘어왔다면 이번 단계는 매우 쉽게 느껴질 것이다.    
> 그만큼 v3가 중요하므로 완벽하게 이해하고 넘어가자.

### 4-6. 유연한 컨트롤러1 - v5

만약 어떤 개발자는 `ControllerV3`방식으로 개발하고 싶고, 어떤 개발자는 `ControllerV4`방식으로 개발하고 싶다면 어떻게 해야 할까?

```
public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
```

```
public interface ControllerV4 {
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```

#### 어댑터 패턴

지금까지 우리가 개발한 프론트 컨트롤러는 한가지 방식의 컨트롤러 인터페이스만 사용할 수 있다.        
`ControllerV3`, `ControllerV4`는 완전히 다른 인터페이스이다. 따라서 호환이 불가능하다. 마치 v3는 110v이고, v4는 220v 전기 콘센트 같은 것이다. 이럴 때 사용하는 것이 바로
어댑터이다.    
어댑터 패턴을 사용해서 프론트 컨트롤러가 다양한 방식의 컨트롤러를 처리할 수 있다록 변경해보자.

#### V5 구조

![](https://i.ibb.co/DMYMc3h/bandicam-2021-06-09-22-39-29-706.jpg)

* **핸들러 어댑터**: 중간에 어댑터 역할을 하는 어댑터가 추가되었는데 이름이 핸들러 어댑터이다. 여기서 어댑터 역할을 해주는 덕분에 다양한 종류의 컨트롤러를 호출할 수 있다.
* **핸들러**: 컨트롤러의 이름을 더 넓은 범위인 핸들러로 변경했다. 그 이유는 이제 어댑터가 있기 때문에 꼭 컨트롤러의 개념 뿐만 아니라 어떠한 것이든 해당하는 종류의 어댑터만 있으면 다 처리할 수 있기
  때문이다.

#### MyHandlerAdapter

* 어댑터용 인터페이스
* `src/main/java/hello/servlet/web/frontcontroller/v5/MyHandlerAdapter.java`

```java
package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyHandlerAdapter {

    boolean supports(Object handler);

    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException;
}

```

* `boolean supports(Object handler)`
    * **handler는 컨트롤러를 말한다!!!**
    * 어댑터가 해당 컨트롤러를 처리할 수 있는지 판단하는 메서드다.
* `ModelView handle(HttpServletRequest request, HttpServletResponse respone, Object handler)`
    * 어댑터는 실제 컨트롤러를 호출하고, 그 결과로 `ModelView`를 반환해야 한다.
    * 실제 컨트롤러가 `ModelView`를 반환하지 못하면, 어댑터가 `ModelView`를 직접 생성해서라도 반환해야 한다.
    * 이전에는 프론트 컨트롤러가 실제 컨트롤러를 호출했지만 이제는 이 어댑터를 통해서 실제 컨트롤러가 호출된다.

#### ControllerV3HandlerAdapter

* `ControllerV3`을 지원하는 어댑터
* `src/main/java/hello/servlet/web/frontcontroller/v5/adapter/ControllerV3HandlerAdapter.java`

```java
package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;

        Map<String, String> paramMap = createParamMap(request);
        return controller.process(paramMap);
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

```

* `handler instance of ControllerV3`
    * `객체 + instanceof + 클래스`
    * 형변환이 가능한 지 해당 여부를 `true` `false`로 반환
    * 부모객체인지 자식 객체인지 확인하는데 쓰임
    * `getClass()`: 현재 참조하고 있는 클래스를 확인

```
ControllerV3 controller = (ControllerV3) handler;
Map<String, String> paramMap = new HashMap<>();
ModelView mv = controller.process(paramMap);
return mv;
```

* handler를 컨트롤러 V3으로 변환한 다음에 V3 형식에 맞도록 호출한다.
* `support()`를 통해 `ControllerV3`만 지원하기 때문에 타입 변환은 걱정없이 실행해도 된다.
* `ControllerV3`는 `ModelView`를 반환하므로 그대로 `ModelView`를 반환하면 된다.

#### FrontControllerServletV5

* `src/main/java/hello/servlet/web/frontcontroller/v5/FrontControllerServletV5.java`

```java
package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object handler = getHandler(request);
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        ModelView mv = adapter.handle(request, response, handler);

        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);

        view.render(mv.getModel(), request, response);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler = " + handler);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

```

* **컨트롤러(Controller) -> 핸들러(Handler)**
    * 이전에는 컨트롤러를 직접 매핑해서 사용했다. 그런데 이제는 어댑터를 사용하기 때문에, 컨트롤러 뿐만 아니라 어댑터가 지원하기만 하면, 어떤 것이라도 URL에 매핑해서 사용할 수 있다. 그래서 이름을
      컨트롤러에서 더 넓은 범위의 핸들러로 변경했다.

* 생성자
    ```java
    public class FrontControllerServletV5() {
        initHandlerMappingMap(); // 핸들러 매핑 초기화
    
        initHandlerAdapters(); // 어댑터 초기화
    }
    ```
    * 생성자는 핸들러 매핑과 어댑터를 초기화(등록)한다.
    * 핸들러가 곧 컨트롤임을 생각해보면 컨트롤를 매핑하는 행위와 동일하다.

* 매핑 정보
  `private final Map<String, Object> handlerMappingMap = new HashMap<>();`
    * 매핑 정보의 값이 `ControllerV3`, `ControllerV4`같은 인터페이스에서 아무 값이나 받을 수 있는 `Object`로 변경되었다.

* 핸들러 매핑
  `Object handler = getHandler(request)`
  ```
  private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
  ```
    * 핸들러 매핑 정보인 `handlerMappingMap`에서 URL에 매핑된 핸들러(컨트롤러)객체를 찾아서 반환한다.

* 핸들러를 처리할 수 있는 어댑터 조회
  `MyHandlerAdapter adapter = getHandlerAdapter(handler)`
    * `handler`를 처리할 수 있는 어댑터를 `adapter.supports(handler)`를 통해서 찾는다.
    * `handler`가 `ControllerV3`인터페이스를 구현했다면, `ControllerV3HandlerAdapter`객체가 반환된다.

* 어댑터 호출
  `ModelView mv = adapter.handle(request, response, handler);`
    * 어댑터의 `handle(request, response, handler)`를 통해서 실제 어댑터가 호출된다.
    * 어댑터는 `handler`(컨트롤러)를 호출하고 그 결과를 어댑터에 맞추어 반환한다.
    * `ControllerV3HandlerAdapter`의 경우 어댑터의 모양과 컨트롤러의 모양이 유사해서 변환 로직이 단순하다.

> MTH   
> 코드를 처음 작성하면 한번에 이해하기가 어려울 수 있다. 단순하게 생각하면 된다.     
> 이전에는 컨트롤러를 `FrontController`에서 호출했다면 이번에는 `FrontController`에서 어댑터를 호출한다. 어댑터는 `Object`형인 `handler`를 알맞은 형(`ControllerV3`)으로 변환을 하고 `handle()`메소드를 통해 `process` 메소드를 실행시켜서 `ModelView`형으로 반환한다.
>
> 변환(어댑터) -> 처리(핸들러 = 컨트롤러)

### 4-7. 유연한 컨트롤러2 - v5

#### FrontControllerServletV5 - ControllerV4 기능 추가

* 코드 추가
* `src/main/java/hello/servlet/web/frontcontroller/v5/FrontControllerServletV5.java`

```java
package hello.servlet.web.frontcontroller.v5;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        // ----------------------- 추가 시작 ----------------------------
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
        // ----------------------- 추가 끝 ----------------------------
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        handlerAdapters.add(new ControllerV4HandlerAdapter()); // 추가---
    }
}
```

* 핸들러 매핑(`handlerMappingMap`)에 `ControllerV4`를 사용하는 컨트롤러를 추가
* 해당 컨트롤러를 처리할 수 있는 어댑터인 `ControllerV4HandlerAdapter` 추가
    * 아직 `ControllerV4HandlerAdapter`를 구현하지 않았으므로 에러 발생

#### ControllerV4HandlerAdapter

* `src/main/java/hello/servlet/web/frontcontroller/v5/adapter/ControllerV4HandlerAdapter.java`

```java
package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v4.ControllerV4;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV4HandlerAdapter implements MyHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof ControllerV4;
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;

        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);

        ModelView mv = new ModelView(viewName);
        mv.setModel(model);

        return mv;
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}

```

* `ControllerV4`에서는 `process()`메소드가 파라미터로 `paramMap`과 `model`를 받아서 `viewName`을 `String`형으로 반환했다.
    * 기존에 `ModelView`의 필드를 이루는 `viewName`과 `model`를 가지고 새로운 `ModelView`객체를 생성해서 반환했다.

* 변환하는 과정은 `ControllerV3HandlerAdapter`와 동일하다.

> MTH       
> `handle()`메소드는 `ModelView`를 반환한다. `ControllerV3HandlerAdapter`를 구현할때는 `ControllerV3`의 `process`가 `ModelView`를 반환했기 때문에 반환값을 그대로 `handle()`의 반환값으로 썼다.     
> 하지만 `ControllerV4`에서는 `process()`외부에서 `model`를 만들고 파라미터로 전달한 후에 반환값은 `viewName`을 가졌다. 처음에는 당황할 수 있으나, 결국에는 `handle`의 반환값인 `ModelView`를 이루는 요소인 `viewName`과 `model`모두 가지고 있기에 새로 객체를 생성해서 반환하면 된다.     
> `mv.setModel(model)`

### 4-8. 정리

지금까지 v1 ~ v5로 점진적으로 프레임워크를 발전시켜 왔다.

* v1: 프론트 컨트롤러를 도입
    * 기존 구조를 최대한 유지하면서 프론트 컨트롤러를 도입

* v2: View 분류
    * 단순 반복 되는 뷰 로직 분리

* v3: Model 추가
    * 서블릿 종속성 제거
    * 뷰 이름 중복 제거

* v4: 단순하고 실용적인 컨트롤러
    * v3과 거의 비슷
    * 구현 입장에서 ModelView를 직접 생성해서 반환하지 않도록 편리한 인터페이스 제공

* v5: 유연한 컨트롤러
    * 어댑터 도입
    * 어댑터를 추가해서 프레임워크를 유연하고 확장성 있게 설계

여기에 애노테이션을 사용해서 컨트롤러를 더 편리하게 발전시킬 수도 있다. 만약 애노테이션을 사용해서 컨트롤러를 편리하게 사용할 수 있게 하려면 어떻게 해야 할까? 바로 애노테이션을 지원하는 어댑터를 추가하면
된다.!        
다형성과 어댑터 덕분에 기존 구조를 유지하면서, 프레임워크의 기능을 확장할 수 있다.

## 5. 스프링 MVC - 구조 이해

### 5-1. 스프링 MVC 전체 구조

#### 직접 만든 프레임워크 -> 스프링 MVC 비교

* 직접 만든 MCV 프레임워크 구조
  ![](https://i.ibb.co/fGvDc2D/bandicam-2021-06-10-11-59-56-364.jpg)

* SpringMVC 구조
  ![](https://i.ibb.co/r7DFCkK/bandicam-2021-06-10-12-00-02-910.jpg)

* 직접 만든 프레임워크 -> 스프링 MVC 비교
    * FrontController -> DispatcherServlet
    * handlerMappingMap -> HandlerMapping
    * MyHandlerAdapter -> HandlerAdapter
    * ModelView -> ModelAndView
    * viewResolver -> ViewResolve
    * MyView -> View

#### DispatcherServlet

`org.springframework.web.servlet.DispatcherServlet`
스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있다.     
스프링 MVC의 프론트 컨트롤러가 바로 디스패처 서블릿(DispacherServlet)이다.     
그리고 이 디스패처 서블릿이 바로 스프링 MVC의 핵심이다.

##### DispacherServlet 서블릿 등록

* `DispatcherServlet`도 부모 클래스에서 `HttpServlet`을 상속 받아서 사용하고, 서블릿으로 동작한다.
    * DispatcherServlet -> FrameworkServlet -> HttpServletBean -> HttpServlet

* 스프링 부트는 `DispacherServlet`을 서블릿으로 자동으로 등록하면서 **모든 경로**`(urlPattern = "/")`에 대해서 매핑한다.
    * 참고: 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

##### 요청 흐름

* 서블릿이 호출되면 `HttpServlet`이 제공하는 `service()`가 호출된다.
* 스프링 MVC는 `DispatcherServlet`의 부모인 `FrameworkServlet`에서 `service()`를 오버라이드 해두었다.
* `FrameworkServlet.service()`를 시작으로 여러 메서드가 호출되면서 `DispatcherServlet.doDispatch()`가 호출된다.

지금부터 `DispacherServlet`의 핵심인 `doDispatch()`코드를 분석해보자. 최대한 간단히 설명하기 위해 예외처리, 인터셉터 기능은 제외했다.

* `DispatcherServlet.doDispatch()`

```java
public class DispatcherServlet extends HttpServlet {
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        ModelAndView mv = null;

        // 1. 핸들러 조회
        MappedHandler = getHandler(processedRequest);
        if (mappedHandler == null) {
            noHandlerFound(processedRequest, response);
            return;
        }

        // 2. 핸틀러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

        // 3. 핸틀러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResonse response, HandlerExcutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {

        // 뷰 렌터링 호출
        render(mv.request, response);

    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {

        View view;
        String viewName = mv.getViewName();

        // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 변환
        view = resolveViewName(viewName, mv.getModelInternal(), locale, request);

        // 8. 뷰 렌더링
        view.render(mv.getModelInternal(), request, response);
    }
}
```

#### SpringMVC 구조

![](https://i.ibb.co/r7DFCkK/bandicam-2021-06-10-12-00-02-910.jpg)

##### 동작 순서

1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다.
4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
    * JSP의 경우: `InternalResourceViewResolver`가 자동 등록되고, 사용된다.
7. View 반환: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다.
    * JSP의 경우 `InternalResourceView(JstlView)`를 반환하는데, 내부에 `forward()`로직이 있다.
8. 뷰 렌더링: 뷰를 통해서 뷰를 렌더링 한다.

#### 인터페이스

##### 인터페이스 살펴보기

* 스프링 MVC의 큰 장점은 `DispatcherServlet`코드의 변경없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다. 지금까지 설명한 대부분을 확장 가능할 수 있게 인터페이스로 제공한다.
* 이 인터페이스들만 구현해서 `DispatcherServlet`에 등록하면 여러분만의 컨트롤러를 만들 수도 있다.

##### 주요 인터페이스 목록

* 핸들러 매핑: `org.springframework.web.servlet.HandlerMapping`
* 핸들러 어댑터: `org.springframework.web.servlet.HandlerAdapter`
* 뷰 리졸버: `org.springframework.web.servlet.ViewResolver`
* 뷰: `org.springframework.web.servlet.View`

### 5-2. 핸들러 매핑과 핸들러 어댑터

과거에 주로 사용했지만 지금은 전혀 사용하지 않는 방식

#### Controller 인터페이스 - 과거 버전 스프링 컨트롤러

* `org.springframework.web.servlet.mvc.Controller`

```java
public interface Controller {

    ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```

> 참고    
> `Controller`인터페이스는 `@Controller`애노테이션과는 전혀 다르다.

##### OldController

* `src/main/java/hello/servlet/web/springmvc/old/OldController.java`

```java
package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("/springmvc/old-controller")
public class OldController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return null;
    }
}

```

* `@Component`: 이 컨트롤러는 `/springmvc/old-controller`라는 이름의 스프링 빈으로 등록되었다.
* **빈의 이름으로 URL을 매핑**할 것이다.

##### 이 컨트롤러는 어떻게 호출되는 것인가?

이 컨트롤러가 호출되려면 다음 2가지가 필요하다.

* HandlerMapping(핸들러 매핑)
    * 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 한다.
    * 예) **스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑**이 필요하다.

* HandlerAdapter(핸들러 어댑터)
    * 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다.
    * 예) `Controller`인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.

스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해두었다. 개발자가 직접 핸들러 매핑과 핸들러 어댑터를 만드는 일은 거의 없다.

##### 스프링 부트가 자동 등록하는 핸들러 매핑과 핸들러 어댑터

실제로는 더 많지만, 중요한 부분 위주로 설명하기 위해 일부 생략

##### HandlerMapping

```
0 = RequestMappingHandlerMapping    :   애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = BeanNameUrlHandlerMapping       :   스프링 빈의 이름으로 핸들러를 찾는다.
```

##### HandlerAdapter

```
0 = RequestMappingHandlerMapping    : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용
1 = HttpRequestHandlerAdapter       : HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter  : Controller 인터페이스(애노테이션x, 과거에 사용)
```

핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고 만약 없으면 다음 순서로 넘어간다.

1. 핸들러 매핑으로 핸들러 조회
    1. `HandlerMapping`을 순서대로 실행해서, 핸들러를 찾는다.
    2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 `BeanNameUrlHandlerMapping`가 실행에 성공하고 핸들러인 `OldController`를
       반환한다.
2. 핸들러 어댑터 조회
    1. `HandlerAdapter`의 `supports()`를 순서대로 호출한다.
    2. `SimpleControllerHandlerAdapter`가 `Controller` 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
    1. 디스패처 서블릿이 조회한 `SimpleControllerHandlerAdapter`를 실행하면서 핸들러 정보도 함께 넘겨준다.
    2. `SimpleControllerHandlerAdapter`는 핸들러인 `OldController`를 내부에서 실행하고, 그 결과를 반환한다.

##### 정리 - OldController 핸들러 매핑, 어댑터

`oldController`를 실행하면서 사용된 객체는 다음과 같다.

* `HandlerMapping = BeanNameUrlHandlerMapping`
* `HandlerAdapter = SimpleControllerHandlerAdapter`

#### HttpRequestHandler

핸들러 매핑과 어댑터를 더 잘 이해하기 위해 Controller 인터페이스가 아닌 다른 핸들러를 알아보자.
`HttpRequestHandler`핸들러(컨트롤러)는 **서블릿과 가장 유사한 형태**의 핸들러이다.

##### HttpRequestHandler

```java
public interface HttpRequestHandler {
    void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletExce3ption, IOException;
}
```

##### MyHttpRequestHandler

* `src/main/java/hello/servlet/web/springmvc/old/MyHttpRequestHandler.java`

```java
package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}

```

1. 핸들러 매핑으로 핸들러 조회
    1. `HandlerMapping`을 순서대로 실행해서, 핸들러를 찾는다.
    2. 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 `BeanNameUrlHandlerMapping`가 실행에 성공하고
       핸들러인 `MyHttpRequestHandler`를 반환한다.

2. 핸들러 어댑터 조회
    1. `HandlerAdapter`의 `supports()`를 순서대로 호출한다.
    2. `HttpRequestHandlerAdapter`가 `HttpRequestHandler`인터페이스를 지원하므로 대상이 된다.

3. 핸들러 어댑터 실행
    1. 디스패처 서블릿이 조회한 `HttpRequestHandlerAdapter`를 실행하면서 핸들러 정보도 함께 넘겨준다.
    2. `HttpRequestHandlerAdapter`는 핸들러인 `MyHttpRequestHandler`를 내부에서 실행하고, 그 결과를 반환한다.

##### 정리 - MyHttpRequestHandler 핸들러매핑, 어댑터

`MyHttpRequestHandler`를 실행하면서 사용된 객체는 다음과 같다.

* `HandlerMapping = BeanNameUrlHandlerMapping`
* `HandlerAdapter = HttpRequestHandlerAdapter`

#### @RequestMapping

조금 뒤에 설명하겠지만, 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`이다.    
`@RequestMapping`의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터이다. 실무에서는 99.9% 이 방식의 컨트롤러를
사용한다.

### 5-3. 뷰 리졸버

#### OldCongtroller - View 조회할 수 있도록 변경

```java
package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("/springmvc/old-controller")
public class OldController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form");
    }
}
```

* View를 사용할 수 있도록 다음 코드 추가
    * `return new ModelAndView("new-form");`

* 웹 브라우저에 `WhiteLabel Error Page`가 나오고, 콘솔에 `OldController.handleRequest`이 출력될 것이다.

#### application.properties 코드 추가

```
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

#### 뷰 리졸버 - InternalResourceViewResolver

스프링 부트는 `InternalResourceViewResolver`라는 뷰 리졸버를 자동으로 등록하는데, 이때 `application.properties`에 등록한 `spring.mvc.view.prefix`
, `spring.mvc.view.suffix`설정 정보를 사용해서 등록한다.

참고로 권장하지는 않지만 설정 없이 다음과 같이 전체 경로를 주어도 동작하기는 한다.
`return new ModelAndView("/WEB-INF/views/new-form.jsp);`

#### 뷰 리졸버 동작 방식

##### 스프링 MVC 구조

![](https://i.ibb.co/r7DFCkK/bandicam-2021-06-10-12-00-02-910.jpg)

##### 스프링 부트가 자동 등록하는 뷰 리졸버

실제로는 더 많지만, 중요한 부분 위주로 설명하기 위해 일부 생략

```
1 = BeanNameViewResolver            : 빈 이름으로 뷰를 찾아서 반환한다.(예: 엑셀 파일 생성 기능에 사용)
2 = InternalResourceViewResolver    : JSP를 처리할 수 있는 뷰를 반환한다. 
```

1. 핸들러 어댑터 호출
    * 핸들러 어댑터를 통해 `new-form`이라는 논리 뷰 이름을 획득한다.

2. ViewResolver 호출
    * `new-form`이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.
    * `BeanNameViewResolver`는 `new-form`이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없다.
    * `InternalResourceViewResolver`가 호출된다.

3. InternalResourceViewResolve
    * 이 뷰 리졸버는 `InternalResourceView`를 반환한다.

4. 뷰 - InternalResourceView
    * `InternalResourceView`는 JSP처럼 포워드`forward()`를 호출해서 처리할 수 있는 경우에 사용한다.

5. view.render()
    * `view.render()`가 호출되고 `InternalResourceView`는 `forward()`를 사용해서 JSP를 실행한다.

> 참고    
> `InternalResourceResolver`는 만약 JSTL 라이브러리가 있으면 `InternalResourceView`를 상속받은 `JstlView`를 반환한다. `JstlView`는 JSTL 태그 사용시 약간의 부가 기능이 추가된다.

> 참고    
> 다른 뷰는 실제 뷰를 렌더링하지만, JSP의 경우 `forward()`통해서 해당 JSP로 이동(실행)해야 렌더링이 된다. JSP를 제외한 나머지 뷰 템플릿들은 `forward()`과정 없이 바로 렌더링 된다.

> 참고    
> Thymeleaf 뷰 템플릿을 사용하면 `ThymeleafViewResolver`를 등록해야 한다. 최근에는 라이브러리만 추가하면 스프링 부트가 이런 작업도 모두 자동화해준다.

### 5-4. 스프링 MVC - 시작하기

스프링이 제공하는 컨트롤러는 애노테이션 기반으로 동작해서, 매우 유연하고 실용적이다. 과거에는 자바 언어에 애노테이션이 없기도 했고, 스프링도 처음부터 이런 유연한 컨트롤러를 제공한 것은 아니다.

#### @RequestMapping

스프링은 애노테이션을 활용한 매우 유연하고, 실용적인 컨트롤러를 만들었는데 이것이 바로 `@RequestMapping` 애노테이션을 사용하는 컨트롤러이다.

* `@RequestMapping`
    * `RequestMappingHandlerMapping`
    * `RequestMappingHandlerAdapter`

앞서 보았듯이 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`이다.  
`@RequestMapping`의 앞글자를 따서 만든 이름인데, 이것이 바로 지금 스프링에서 주로 사용하는 애노테이션 기반의 컨트롤러를 지원하는 핸들러 매핑과 어댑터이다. **실무에서는 99.9% 이 방식의
컨트롤러를 사용한다.**

#### SpringMemberFormControllerV1

* 회원 등록 폼
* `src/main/java/hello/servlet/web/springmvc/v1/SpringMemberFormControllerV1.java`

```java
package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpringMemberFormControllerV1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process() {
        return new ModelAndView("new-form");
    }
}

```

* `@Controller`
    * 스프링이 자동으로 스프링 빈으로 등록한다.(내부에 `@Component`애노테이션이 있어서 컴포넌트 스캔의 대상이 됨)
    * 스프링 MVC에서 애노테이션 기반 컨트롤러로 인식한다.

* `@RequestMapping`
    * 요청 정보를 매핑한다. 해당 URL이 호출되면 이 메서드가 호출된다.
    * 애노테이션을 기반으로 동작하기 때문에, 메서드의 이름은 임의로 지으면 된다.

* `ModelAndView`
    * 모델과 뷰 정보를 담아서 반환하면 된다.
    * 내가 만든 `ModelView`와 같다고 생각하면 된다.

#### SpringMemberSaveControllerV1

* 회원 저장
* `src/main/java/hello/servlet/web/springmvc/v1/SpringMemberSaveControllerV1.java`

```java
package hello.servlet.web.springmvc.v1;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SpringMemberSaveControllerV1 {

    private final MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);
        return mv;
    }
}

```

* `mv.addObject("member", member)`
    * 스프링이 제공하는 `ModelAndView`를 통해 Model 데이터를 추가할 때는 `addObject()`를 사용하면 된다. 이 데이터는 이후 뷰를 렌더링 할 때 사용된다.
    * 내가 만든 `ModelView`의 객체에서 `mv.getModel().put("member", member)`와 같은 작업을 수행한다고 보면 된다.

#### SpringMemberListControllerV1

* 회원 목록
* `src/main/java/hello/servlet/web/springmvc/v1/SpringMemberListControllerV1.java`

```java
package hello.servlet.web.springmvc.v1;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class SpringMemberListControllerV1 {

    private final MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {
        List<Member> members = memberRepository.findAll();

        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", members);
        return mv;
    }
}

```



# Note

* IntelliJ 무료버전일때 `War`의 경우 톰캣이 정상 시작되지 않는 경우가 생김
    * 이땐 build를 gradle로 다시 설정하거나
    * `provideRuntime 'org.springframework.boot:spring-boot-starter-tomcat`을 제거해야 한다.