package com.elon33.netdisc.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.elon33.netdisc.model.HdfsDAO;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapred.JobConf;


/**
 * Servlet implementation class ListServlet
 */
public class LoginServlet extends HttpServlet {
 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		com.elon33.netdisc.model.UserBeanCl ubc = new com.elon33.netdisc.model.UserBeanCl();
		if(ubc.checkUser(username, password)){
			//用户合法，跳转到界面
			HttpSession session = request.getSession(); 
			session.setAttribute("username", username);
			session.setAttribute("currentPath", HdfsDAO.getHdfs()+"/"+username);
			JobConf conf = HdfsDAO.config();
	        HdfsDAO hdfs = new HdfsDAO(conf);

			FileStatus[] list = hdfs.ls("/"+username);
			request.setAttribute("list",list);
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}else{
			//用户不合法，调回登录界面，并提示错误信息
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}

		
	 
	}

}
