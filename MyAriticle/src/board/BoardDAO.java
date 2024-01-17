package board;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import common.JDBConnect;

public class BoardDAO extends JDBConnect {
    public BoardDAO(ServletContext application) {
        super(application);
    }

    
    public int selectCount(Map<String, Object> map) {
        int totalCount = 0; 

        String query = "SELECT COUNT(*) FROM board";
        if (map.get("searchWord") != null) {
            query += " WHERE " + map.get("searchField") + " "
                   + " LIKE '%" + map.get("searchWord") + "%'";
        }

        try {
            stmt = con.createStatement();   
            rs = stmt.executeQuery(query);  
            rs.next();  
            totalCount = rs.getInt(1);  
        }
        catch (Exception e) {
            System.out.println("게시물 수를 구하는 중 예외 발생");
            e.printStackTrace();
        }

        return totalCount; 
    }

    public List<BoardDTO> selectListPage(Map<String, Object> map) {
        List<BoardDTO> bbs = new Vector<BoardDTO>();  
         
        String query = " SELECT * FROM ( "
                     + "    SELECT Tb.*, ROWNUM rNum FROM ( "
                     + "        SELECT * FROM board ";


        if (map.get("searchWord") != null) {
            query += " WHERE " + map.get("searchField")
                   + " LIKE '%" + map.get("searchWord") + "%' ";
        }
        
        query += "      ORDER BY num DESC "
               + "     ) Tb "
               + " ) "
               + " WHERE rNum BETWEEN ? AND ?"; 

        try {

            psmt = con.prepareStatement(query);
            psmt.setString(1, map.get("start").toString());
            psmt.setString(2, map.get("end").toString());
            
            
            rs = psmt.executeQuery();
            
            while (rs.next()) {
                
                BoardDTO dto = new BoardDTO();
                dto.setNum(rs.getString("num"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getDate("postdate"));
                dto.setId(rs.getString("id"));
                dto.setVisitcount(rs.getString("visitcount"));
                dto.setRef(rs.getInt("ref"));
                dto.setRe_step(rs.getInt("re_step"));
                dto.setRe_level(rs.getInt("re_level"));
                
                bbs.add(dto);
            }
        } 
        catch (Exception e) {
            System.out.println("게시물 조회 중 예외 발생");
            e.printStackTrace();
        }
        
        
        return bbs;
    }

    public int insertWrite(BoardDTO dto) {
        int result = 0;
        int ref = 0; // 글그룹을 의미한다, 쿼리를 실행시켜서 가장 큰 ref값을 가져온후 +1을 더해주면 된다.
        int re_step = 1; // 새글이고, 부모글은 글 스텝(Re_step), 글 레벨(Re_level)은 1이기 때문에 초기값을 1을 준다.
        int re_level = 1; // 새글이고, 부모글은 글 스텝(Re_step), 글 레벨(Re_level)은 1이기 때문에 초기값을 1을 준다.
 
        
        try {
            String query = "INSERT INTO board ( "
                         + " num,title,content,id,visitcount,ref,re_step,re_level) "
                         + " VALUES ( "
                         + " seq_board_num.NEXTVAL, ?, ?, ?, 0, ?, ?, ?)";  

            psmt = con.prepareStatement(query);  
            psmt.setString(1, dto.getTitle());  
            psmt.setString(2, dto.getContent());
            psmt.setString(3, dto.getId());  
            psmt.setInt(4,  dto.getRef());
            psmt.setInt(5, dto.getRe_step());
            psmt.setInt(6, dto.getRe_level());
            result = psmt.executeUpdate(); 
        }
        catch (Exception e) {
            System.out.println("게시물 입력 중 예외 발생");
            e.printStackTrace();
        }
        
        return result;
    }


    // 지정한 게시물을 찾아 내용을 반환합니다.
    public BoardDTO selectView(String num) { 
        BoardDTO dto = new BoardDTO();

        String query = "SELECT B.*, M.name " 
                     + " FROM member M INNER JOIN board B " 
                     + " ON M.id=B.id "
                     + " WHERE num=?";

        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, num);    // 인파라미터를 일련번호로 설정 
            rs = psmt.executeQuery();  

            
            if (rs.next()) {
                dto.setNum(rs.getString(1)); 
                dto.setTitle(rs.getString(2));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getDate("postdate"));
                dto.setId(rs.getString("id"));
                dto.setVisitcount(rs.getString(6));
                dto.setName(rs.getString("name"));
                dto.setRef(rs.getInt("ref"));
                dto.setRe_step(rs.getInt("re_step"));
                dto.setRe_level(rs.getInt("re_level"));
            }
        } 
        catch (Exception e) {
            System.out.println("게시물 상세보기 중 예외 발생");
            e.printStackTrace();
        }
        
        return dto; 
    }

    // 지정한 게시물의 조회수를 1 증가시킵니다.
    public void updateVisitCount(String num) { 
        
        String query = "UPDATE board SET "
                     + " visitcount=visitcount+1 "
                     + " WHERE num=?";
        
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, num);  // 인파라미터를 일련번호로 설정 
            psmt.executeQuery();     // 쿼리 실행 
        } 
        catch (Exception e) {
            System.out.println("게시물 조회수 증가 중 예외 발생");
            e.printStackTrace();
        }
    }
    
    
    public int updateEdit(BoardDTO dto) { 
        int result = 0;
        
        try {
            // 쿼리 템플릿
            String query = "UPDATE board SET "
                         + " title=?, content=? "
                         + " WHERE num=?";
            
            // 쿼리문 완성
            psmt = con.prepareStatement(query);
            psmt.setString(1, dto.getTitle());
            psmt.setString(2, dto.getContent());
            psmt.setString(3, dto.getNum());
            
            // 쿼리문 실행 
            result = psmt.executeUpdate();
        } 
        catch (Exception e) {
            System.out.println("게시물 수정 중 예외 발생");
            e.printStackTrace();
        }
        
        return result; 
    }

    
    public int deletePost(BoardDTO dto) { 
        int result = 0;

        try {
            // 쿼리문 템플릿
            String query = "DELETE FROM board WHERE num=?"; 

            // 쿼리문 완성
            psmt = con.prepareStatement(query); 
            psmt.setString(1, dto.getNum()); 

           
            result = psmt.executeUpdate(); 
        } 
        catch (Exception e) {
            System.out.println("게시물 삭제 중 예외 발생");
            e.printStackTrace();
        }
        
        return result; 
}
