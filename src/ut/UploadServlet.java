package ut;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {
	         

	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 200 * 1024;
	private int maxMemSize = 24 * 1024;
	private File file;

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getInitParameter("file-upload");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("w");
		java.io.PrintWriter out = response.getWriter();
		UploadServlet obj = new UploadServlet();
		if (!isMultipart) {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>");
			out.println("</body>");
			out.println("</html>");
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("/home/atilio/temp/temp2/"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		try {
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					// Write the file
					if (fileName.lastIndexOf("\\") >= 0) {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\")));
					} else {
						file = new File(
								filePath
										+ fileName.substring(fileName
												.lastIndexOf("\\") + 1));
					}
					fi.write(file);

					// Executing the class with Dalvik VM
					String fileNameShort = fileName.replaceAll(".java", "");
					String fileNameJar = (fileNameShort + ".jar");
					String fileNameClass = (fileNameShort + ".class");
					String dir = "/home/ubuntu/android-x86/";
//					String localdir = "/home/atilio/temp/";
					out.print("G02" + "<br>");

					// Executing javac command
					String[] command0 = { "sh", "-c", "javac " + dir + fileName };
					obj.executeCommand(command0);
					

					// Executing dx command
					List list0 = new ArrayList();
					list0.add("dx");
					list0.add("--dex");
					list0.add("--output");
					list0.add(fileNameJar);
					list0.add(fileNameClass);
					Process p0 = null;
					ProcessBuilder pb0 = new ProcessBuilder(list0);
					pb0.directory(new File(dir));
					pb0.start();
					

					// Executing rund.sh command
					List list1 = new ArrayList();
					list1.add("./rund.sh");
					list1.add("-cp");
					list1.add(fileNameJar);
					list1.add(fileNameShort);
					Process p1 = null;
					ProcessBuilder pb1 = new ProcessBuilder(list1);
					pb1.directory(new File(dir));
					pb1.start();
					
					out.println("Uploaded Filename: " + fileName + "<br>");
				}
			}
			out.println("</body>");
			out.println("</html>");
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		throw new ServletException("GET method used with "
				+ getClass().getName() + ": POST method required.");
	}

	public String executeCommand(String[] commanda) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(commanda);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}

}
