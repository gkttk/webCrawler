<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html;charset=cp1251"%>
<!DOCTYPE HTML>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <title>Crawler</title>
</head>
<body>


<form id="form1" action="${pageContext.request.contextPath}/crawl" method="post">
    <fieldset>
        <div>
            <input type="text" id="url" name="url" required="required"/>
            <label for="url">URL</label>
        </div>
        <div class="form-group">
            <input type="text" id="depth" name="depth" required="required" pattern="[0-9]+"/>
            <label for="depth">Depth</label>
        </div>
        <div class="form-group">
            <input type="text" id="words1" name="words" required="required"/>
            <label for="words1">Word</label><br>
            <input type="text" id="words2" name="words" required="required"/>
            <label for="words2">Word</label><br>
            <input type="text" id="words3" name="words" required="required"/>
            <label for="words3">Word</label><br>
        </div>
        <br>
        <button type="submit">Crawl</button>
    </fieldset>
</form>


<c:if test="${sessionScope.get('result') != null}">
    <h1>Таблица результата</h1>

    <c:set var="crawlingResult" value="${sessionScope.get('result')}"/>

    <table class="table">
        <thead class="thead-dark">
        <tr>
            <th scope="col">URL</th>
            <c:forEach items="${crawlingResult.getWords()}" var="word">
                <th scope="row">${word}</th>
            </c:forEach>
            <th scope="col">Total</th>
        </tr>
        </thead>
        <tr>
            <th scope="col">${crawlingResult.getURL()}</th>
            <c:forEach items="${crawlingResult.getWords()}" var="word">
                <th scope="row">${crawlingResult.countWordOnPage(word)}</th>
            </c:forEach>
            <th scope="col">${crawlingResult.totalWordsResult()}</th>
        </tr>
    </table>

    <h1>Топ 10 результатов</h1>
    <ul class="list-group list-group-flush">
        <c:forEach items="${crawlingResult.getTopURLs()}" var="topUrl"> <%--<c:forEach items="${crawlingResult.getPagesWithMaxResultsOut()}" var="topResults">--%>
            <li class="list-group-item">${crawlingResult.getInfoTopResult(topUrl)}</li> <%-- <li class="list-group-item">${topResults.toString()}</li>--%>
        </c:forEach>
    </ul>


    <br>
    <form id="form2" action="/downloadResult" method="get">
        <input type="submit" form="form2" value="DownloadResult"/>
    </form>
</c:if>


</body>


</html>