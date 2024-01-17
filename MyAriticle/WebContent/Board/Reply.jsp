<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./IsLoggedIn.jsp"%> <!--로그인 확인-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원제 게시판</title>
<script type="text/javascript">
function validateForm(form) {  // 폼 내용 검증
    if (form.title.value == "") {
        alert("제목을 입력하세요.");
        form.title.focus();
        return false;
    }
    if (form.content.value == "") {
        alert("내용을 입력하세요.");
        form.content.focus();
        return false;
    }
}
</script>

</head>
<body>
<jsp:include page="../Common/Link.jsp" />
<%
      int num = Integer.parseInt(request.getParameter("num"));
      int ref = Integer.parseInt(request.getParameter("ref"));
      int re_step = Integer.parseInt(request.getParameter("re_step"));
      int re_level = Integer.parseInt(request.getParameter("re_level"));
        %>
<h2>답글쓰기</h2>
<form name="replyFrm" method="post" action="ReplyProcess.jsp"
      onsubmit="return validateForm(this);">
    <table border="1" width="90%">
        <tr>
            <td>제목</td>
            <td>
                <input type="text" name="title" style="width: 90%;" />
            </td>
        </tr>
        <tr>
            <td>내용</td>
            <td>
                <textarea name="content" style="width: 90%; height: 100px;"></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
            <input type="hidden" name="ref" value="<%=ref%>"> 
            <input type="hidden" name="re_step" value="<%=re_step%>"> 
            <input type="hidden" name="re_level" value="<%=re_level%>"> 
            <button type="submit">작성 완료</button>
            <button type="reset">다시 입력</button>
            <button type="button" onclick="location.href='List.jsp';">
                    목록 보기</button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>