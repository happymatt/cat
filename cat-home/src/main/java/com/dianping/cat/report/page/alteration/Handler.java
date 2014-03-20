package com.dianping.cat.report.page.alteration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;

import com.dianping.cat.Cat;
import com.dianping.cat.home.dal.report.Alteration;
import com.dianping.cat.home.dal.report.AlterationDao;
import com.dianping.cat.report.ReportPage;

import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;
	
	@Inject
	private AlterationDao m_alterationDao;
	
	private SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "alteration")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "alteration")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();
		
		String type = payload.getType();
		String title = payload.getTitle();
		String domain = payload.getDomain();
		String ip = payload.getIp();
		String user = payload.getUser();
				
		switch(action){
		case INSERT: 
			String content = payload.getContent();
			String url = payload.getUrl();
			String date = payload.getDate();
			
			Alteration alt = new Alteration();	
			alt.setType(type);
			alt.setTitle(title);
			alt.setDomain(domain);
			alt.setIp(ip);
			alt.setUser(user);
			alt.setContent(content);
			alt.setUrl(url);			
			
			try {
				alt.setDate(m_sdf.parse(date));
				
				m_alterationDao.insert(alt);
				model.setStatus("{\"status\":\"success\"}");
			} catch (Exception e) {
				Cat.logError(e);
				model.setStatus("{\"status\":\"fail\"}");	
			}
			break;
		case VIEW:
			String startTime = payload.getStartTime();
			String endTime = payload.getEndTime();
			long granularity = payload.getGranularity();
			
			//model.setResult(m_manager.getEmailPassword()+  " action2  " + domain+" " +ip);
			break;
		}

		model.setAction(action);
		model.setPage(ReportPage.ALTERATION);

		if (!ctx.isProcessStopped()) {
		   m_jspViewer.view(ctx, model);
		}
	}
}
