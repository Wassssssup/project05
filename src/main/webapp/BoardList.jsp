<%@page import="model.BoardBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div align="center">
	<h2>전체 게시글 보기</h2>
	<%
		//화면에 보여질 게시글의 개수
		int pageSize=10;
	
		String pageNum=request.getParameter("pageNum");
		
		// 만약 처음이다 = 페이지Num의 값이 없기 때문에 초기값을 세팅
		if(pageNum == null ){
			pageNum="1";
		}
		
		int count=0; //전체 글
		int number=0; //페이지 넘버링
		
		// 현재 보고자하는 페이지 숫자를 지정
		int currentPage=Integer.parseInt(pageNum);
		
		BoardDAO bdao = new BoardDAO();
		
		// 전체 게시글의 개수
		count=bdao.getAllCount();
		
		//현재 페이지에 보여줄 시작 번호를 설정
		int startRow=(currentPage-1)*pageSize+1; 
		//(currentPage*pageSize)-(pageSize-1);
		int endRow = currentPage*pageSize;
		
		Vector<BoardBean> vec=bdao.getAllBoard(startRow, endRow);
		
		number =count - (currentPage-1)*pageSize;
	%>
	
	<table width="700" boarder="1" bgcolor="skyblue">
		<tr height="40">
			<td align="right" colspan="5">
				<input type="button" value="글쓰기" onclick="location.href='BoardWriteForm.jsp'">
			</td>
		</tr>
		<tr height="40">
			<td width="50" align="center">번호</td>
			<td width="320" align="center">제목</td>
			<td width="100" align="center">작성자</td>
			<td width="150" align="center">작성일</td>
			<td width="80" align="center">조회수</td>
		</tr>		
		<%
		for(int i=0;i<vec.size();i++){
			BoardBean bean=vec.get(i);
		%>
			<tr height="40">
				<td width="50" align="center"><%=number-- %></td>
				<td width="320" align="left">
					<a href="BoardInfo.jsp?num=<%=bean.getNum() %>" style="text-decoration:none;">
			
		<%
		//들여쓰기 하는 부분
			if(bean.getRe_step() > 1){
				for(int j=0;j<(bean.getRe_step()-1)*5;j++){
		%> &nbsp;
		<%
				}
			}
		%>
		<%=bean.getSubject() %>
			</a>
		</td>
		<td width="100" align="center"><%=bean.getWriter() %></td>
		<td width="150" align="center"><%=bean.getReg_date() %></td>
		<td width="80" align="center"><%=bean.getReadcount() %></td>
		</tr>
		<%
		}
		%>
	</table>
	<p>
	<% 
		int pageCount=0;
		int startPage=1;
		int pageBlock=10;
		
		if(count > 0){
			pageCount=count/pageSize+(count%pageSize == 0 ? 0 : 1);
			
			if(currentPage%10 != 0){
				startPage=(currentPage/10)*10+1;
			}else{
				//startPage=((currentPage/10)-1)*10+1;
				startPage=(currentPage-(pageBlock-1));
			}
			
			int endPage=startPage+(pageBlock-1);
			
			if(endPage > pageCount)
				endPage=pageCount;
			
			if(startPage > 10){
	%>
			<a href="BoardList.jsp?pageNum=<%=startPage-10 %>">[이전]></a>
	<%
			}
			for(int i=startPage;i<=endPage;i++){
	%>
			<a href="BoardList.jsp?pageNum=<%=i %>">[<%=i %>]</a>
	<%
			}
			
			if(endPage < pageCount){
	%>			
			<a href="BoardList.jsp?pageNum=<%=startPage+10%>">[다음]></a>
	<%
			}
		}
	%>	
	</p>
</div>
</body>
</html>