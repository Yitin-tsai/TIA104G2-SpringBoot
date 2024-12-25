package chilltrip.location.model;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import chilltrip.location.dao.LocationDAOImplJDBC;

@WebServlet("/LocationList")
public class LocationList extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");

		String location_name = req.getParameter("location_name");

		if (location_name.trim().length() == 0) {
			try (LocationDAOImplJDBC builder = new LocationDAOImplJDBC()) {
				List<Map<String, Object>> locationlist = builder.getAllPro();
//				out.println(locationlist);

				// 用getallpro把物件拆成 key/value的方式放到表格中
				StringBuilder tableHtml = new StringBuilder();
				tableHtml.append("<table border='1'><thead><tr>");

				if (!locationlist.isEmpty()) {
					Map<String, Object> firstRow = locationlist.get(0);
					for (String column : firstRow.keySet()) {
						tableHtml.append("<th>").append(column).append("</th>");
					}
					tableHtml.append("</tr></thead><tbody>");

					for (Map<String, Object> row : locationlist) {
						tableHtml.append("<tr>");
						for (Object value : row.values()) {
							tableHtml.append("<td>").append(value).append("</td>");
						}
						tableHtml.append("</tr>");
					}
					tableHtml.append("</tbody></table>");
				} else {
					tableHtml.append("<tr><td colspan='2'>無資料</td></tr></tbody></table>");
				}

				res.getWriter().write("<dev id='result'>" + tableHtml.toString() + "</dev>");

				// 方法一：測試把後端資料丟給瀏覽器解析-->瀏覽器不認識list怎麼辦（？）-->只能在後端處理好再丟？
//				out.println("<div class=table>");
//				out.println("<table style=font-size:20px;>");
//				out.println("<tr>");
//				out.println("<td>景點</td>");
//				out.println("<td>地址</td>");
//				out.println("<td>創建時間</td>");
//				out.println("<td>評論人數</td>");
//				out.println("<td>平均評分</td>");
//				out.println("<td>地點名稱</td>");
//				out.println("</tr>");
//				out.println("</table>");
//				out.println("<table id = table></table>");
//				out.println("</div>");
//				
//				out.println("<script>");
//				out.println("var strs='';");
//				out.println("for (var i = 0; i< list.size(); i++) {"+ locationlist + " = list.get(i);");
//				out.println("var str='';");
//				out.println("str += <td> + i+1 + </td>;");
//				out.println("str += <td>" +locationlist+ ".getB()  + </td>;");
//				out.println("str += <td>" +locationlist+ ".getC()  + </td>;");
//				out.println("str += <td>" +locationlist+ ".getD()  + </td>;");
//				out.println("str += <td>" +locationlist+ ".getE()  + </td>;");
//				out.println("str += <td>" +locationlist+ ".getF()  + </td>;");
//				out.println("strs += <tr>+</tr>;");
//				out.println("}");
//				
//				out.println("document.getElementById(#table).innerHTML=strs;");
//				out.println("</script>");

			}
		} else
			try {
				System.out.println(location_name);
				if (location_name.trim().length() != 0) { // 不為空-->根據查詢條件列出物件
					try (LocationDAOImplJDBC builder = new LocationDAOImplJDBC()) {
						List<Map<String, Object>> locationlist = builder.getLocationByName(location_name.trim());

						StringBuilder tableHtml = new StringBuilder();
						tableHtml.append("<table border='1'><thead><tr>");

						if (!locationlist.isEmpty()) {
							Map<String, Object> firstRow = locationlist.get(0);
							for (String column : firstRow.keySet()) {
								tableHtml.append("<th>").append(column).append("</th>");
							}
							tableHtml.append("</tr></thead><tbody>");

							for (Map<String, Object> row : locationlist) {
								tableHtml.append("<tr>");
								for (Object value : row.values()) {
									tableHtml.append("<td>").append(value).append("</td>");
								}
								tableHtml.append("</tr>");
							}
							tableHtml.append("</tbody></table>");
						} else {
							tableHtml.append("<tr><td colspan='2'>無資料</td></tr></tbody></table>");
						}

						res.getWriter().write("<dev id='result'>" + tableHtml.toString() + "</dev>");

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//		}
//		out.println("<HTML>");
//		out.println("<HEAD><TITLE>所有景點, " + location_name + "</TITLE></HEAD>");
//		out.println("<BODY>");
//		out.println("Hello, 你好: " + location_name);
//		out.println("</BODY></HTML>");

	}
}
