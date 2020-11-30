package com.elon33.netdisc.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapred.JobConf;

import com.elon33.netdisc.model.HdfsDAO;

/**
 * Servlet implementation class DownloadServlet
 */
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext context = getServletContext();
		//String local = context.getInitParameter("file-download");
		String local = request.getSession().getServletContext().getRealPath("download");
		System.out.println("local:" + local + "");
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		String filePath = new String(request.getParameter("filePath").getBytes("ISO-8859-1"), "GB2312");
		System.out.println(filePath);
		JobConf conf = HdfsDAO.config();
		HdfsDAO hdfs = new HdfsDAO(conf);
		String filename=filePath.substring(filePath.lastIndexOf("/"));
		System.out.println("filename:" + filename + "");
		String localFileName=local+filename;
		hdfs.download(filePath, localFileName);

		//FileStatus[] list = hdfs.ls((String)session.getAttribute("currentPath"));
		//request.setAttribute("list", list);
		//request.getRequestDispatcher("index.jsp").forward(request,response);

		response.setStatus(200);
		//response.setContentType("image/jpeg");
		String[] array = localFileName.split("[.]");
		String fileType = array[array.length-1].toLowerCase();
		System.out.println("fileType:"+fileType);
//                    //设置文件ContentType类型
		if("jpg,jepg,gif,png".contains(fileType)){//图片类型
			response.setContentType("image/"+fileType);
		}else if("pdf".contains(fileType)){//pdf类型
			response.setContentType("application/pdf");
		}else if("mp4".contains(fileType)){//mp4类型
			response.setContentType("video/mpeg4");
		}else if("mpeg".contains(fileType)){//mpeg类型
			response.setContentType("video/mpg");
		}else if("doc,docx".contains(fileType)){//word类型
			response.setContentType("application/msword");
		}else if("xls,xlsx".contains(fileType)){//excel类型
			response.setContentType("application/vnd.ms-excel");
		}else{//自动判断下载文件类型
			response.setContentType("multipart/form-data");
		}
		//文件输入流读取资源
		FileInputStream fileInputStream = new FileInputStream(local+filename);
		//获取文件名
		//substring() 方法返回字符串的子字符串。
		//lastIndexOf() 方法可返回一个指定的字符串值最后出现的位置，
		// 在一个字符串中的指定位置从后向前搜索
		String fileName = localFileName.substring(localFileName.lastIndexOf("/")+1);
		//设置消息头，告诉浏览器，我要下载这个文件
		//response.setHeader("Content-Disposition", "attachment; filename="+fileName);
		response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName,"UTF-8"));
		int len = 0;
		byte[] bytes = new byte[1024];
		ServletOutputStream  outputStream = response.getOutputStream();
		while((len=fileInputStream.read(bytes))>0){
			outputStream.write(bytes, 0, len);
		}
		outputStream.flush();
		//关闭资源
		outputStream.close();
		fileInputStream.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
