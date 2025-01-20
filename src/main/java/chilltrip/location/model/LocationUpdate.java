package chilltrip.location.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chilltrip.location.dao.LocationDAOImplJDBC;

@WebServlet("/LocationUpdate")
public class LocationUpdate extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");

		Integer location_id = Integer.valueOf(req.getParameter("location_id"));

		String address = req.getParameter("address");
		String create_time = req.getParameter("create_time");
		Integer comments_number = Integer.valueOf(req.getParameter("comments_number"));
		Float score = Float.valueOf(req.getParameter("score"));
		String location_name = req.getParameter("location_name");

		LocationVO location = new LocationVO();
		location.setLocationId(location_id);
		location.setAddress(address);
		location.setComments_number(comments_number);
		location.setScore(score);
		location.setLocation_name(location_name);
//		System.out.println(location);

		try (LocationDAOImplJDBC updateLocation = new LocationDAOImplJDBC()) {
			updateLocation.update(location);
			List<Map<String, Object>> locationlist = updateLocation.getAllPro();
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

}
