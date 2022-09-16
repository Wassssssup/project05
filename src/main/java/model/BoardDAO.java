package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.Vector;

/*import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;*/

public class BoardDAO {
	ResultSet rs;
	Connection conn;
	PreparedStatement pstmt;

	public void getCon() {
		try {
			String dbURL="jdbc:oracle:thin:@localhost:1521:xe";
			String dbID="scott";
			String dbPassword="tiger";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn=DriverManager.getConnection(dbURL,dbID,dbPassword);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	public void getCon() {
		try {
			Context initctx=new InitialContext();
			Context envctx=(Context)initctx.lookup("java:/comp/env");
			DataSource ds = (DataSource)envctx.lookup("jdbc/Oracle11g");
			conn=ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	*/
	//하나의 게시글을 저장하는 메서드
	public void insertBoard(BoardBean bean) {	
		getCon();
		
		int ref=0;	//그룹 (org_글)
		int re_step=1; // 댓글 단위용 (재작)
		int re_level=1; // 
		
		try {
			String refsql="select max(ref) from board";
			pstmt = conn.prepareStatement(refsql);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				ref=rs.getInt(1)+1;
			}
			
			String sql = "insert into board values(board_seq.nextval,?,?,?,?,sysdate,?,?,?,0,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bean.getWriter());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5,ref);
			pstmt.setInt(6, re_step);
			pstmt.setInt(7, re_level);
			pstmt.setString(8, bean.getContent());
			
			pstmt.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
		public Vector<BoardBean> getAllBoard(int start,int end){
			Vector<BoardBean> vec = new Vector<>();
			getCon();
			
			try {
				String sql ="select * from(select A.*,Rownum Rnum from(select * from board order by ref desc,re_step asc)A)"+"where Rnum >= ? and Rnum <= ?";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					BoardBean bean = new BoardBean();
					bean.setNum(rs.getInt(1));
					bean.setWriter(rs.getString(2));
					bean.setEmail(rs.getString(3));
					bean.setSubject(rs.getString(4));
					bean.setPassword(rs.getString(5));
					bean.setReg_date(rs.getDate(6).toString());
					bean.setRef(rs.getInt(7));
					bean.setRe_step(rs.getInt(8));
					bean.setRe_level(rs.getInt(9));
					bean.setReadcount(rs.getInt(10));
					bean.setContent(rs.getString(11));
					vec.add(bean);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return vec;
		}
		
		public BoardBean getOneBoard(int num) {
			BoardBean bean = new BoardBean();
			getCon();
			
			try {
				String readsql = "update board set readcount = readcount+1 where num =?";
				pstmt =conn.prepareStatement(readsql);
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
				
				String sql = "select * from board where num =?";
				
				pstmt =conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				
				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					bean.setNum(rs.getInt(1));
					bean.setWriter(rs.getString(2));
					bean.setEmail(rs.getString(3));
					bean.setSubject(rs.getString(4));
					bean.setPassword(rs.getString(5));
					bean.setReg_date(rs.getDate(6).toString());
					bean.setRef(rs.getInt(7));
					bean.setRe_step(rs.getInt(8));
					bean.setRe_level(rs.getInt(9));
					bean.setReadcount(rs.getInt(10));
					bean.setContent(rs.getString(11));
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return bean;
		}
		
		public void reWriteBoard(BoardBean bean) {
			int ref = bean.getRef();
			int re_step = bean.getRe_step();
			int re_level = bean.getRe_level();
			
			getCon();
			
			try {
				String levelsql="update board set re_level=re_level+1 where ref=? and re_level > ?";
				pstmt = conn.prepareStatement(levelsql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_level);
				pstmt.executeUpdate();
				
				//답변글 데이터를 저장
				String sql="insert into board values(board_seq.nextval,?,?,?,?,sysdate,?,?,?,0,?)";
				
				pstmt = conn.prepareStatement(sql); //★
								
				pstmt.setString(1, bean.getWriter());
				pstmt.setString(2, bean.getEmail());
				pstmt.setString(3, bean.getSubject());
				pstmt.setString(4, bean.getPassword());
				pstmt.setInt(5,ref);
				pstmt.setInt(6, re_step +1);
				pstmt.setInt(7, re_level +1);
				pstmt.setString(8, bean.getContent());
				
				pstmt.executeUpdate();
				
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//num에 해당하는 객체를 업데이트.
		public BoardBean getOneUpdateBoared(int num) {
			BoardBean bean = new BoardBean();
			getCon();
			
			try {
				String sql="select * from board where num=?";
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					bean.setNum(rs.getInt(1));
					bean.setWriter(rs.getString(2));
					bean.setEmail(rs.getString(3));
					bean.setSubject(rs.getString(4));
					bean.setPassword(rs.getString(5));
					bean.setReg_date(rs.getDate(6).toString());
					bean.setRef(rs.getInt(7));
					bean.setRe_step(rs.getInt(8));
					bean.setRe_level(rs.getInt(9));
					bean.setReadcount(rs.getInt(10));
					bean.setContent(rs.getString(11));
				}
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return bean;
		}
		
		public String getPass(int num) {
			String pass="";
			getCon();
			
			try {
				String sql="select password from board where num=?";
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1,num);
				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					pass=rs.getString(1);
				}
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			return pass;
		}
		
		//하나의 게사글을 수정하는 메서드
		public void updateBoard(BoardBean bean) {
			getCon();
			
			try {
				String sql = "update board set subject=?,content=? where num=?";
				pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, bean.getSubject());
				pstmt.setString(2, bean.getContent());
				pstmt.setInt(3, bean.getNum());
				
				pstmt.executeUpdate();
				
				conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void deleteBoard(int num) {
			getCon();
			
			try {
				String sql="delete from board where num=?";
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
				
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();				
			}
			
		}
		
		//전체 글의 개수를 리턴하는 메서드
		public int getAllCount() {
			getCon();
			int count=0;
			
			try {
				String sql="select count(*) from board";
				pstmt=conn.prepareStatement(sql);
				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					count=rs.getInt(1);
				}
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			return count;
		}
		
	}
	



