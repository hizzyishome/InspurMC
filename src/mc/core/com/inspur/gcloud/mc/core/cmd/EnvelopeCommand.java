/**
 * 
 */
package com.inspur.gcloud.mc.core.cmd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.loushang.framework.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.inspur.gcloud.mc.core.data.Envelope;
import com.inspur.gcloud.mc.core.data.Message;
import com.inspur.gcloud.mc.core.service.IEnvelopeService;
import com.inspur.gcloud.mc.core.service.IMessageService;


/**
 * Controller层，用于前后台交互、前后台数据格式转换
 * 信封Command
 * @author ZXh
 *
 */
@Controller
@RequestMapping(value = "/mc/core")
public class EnvelopeCommand {
	
	@Autowired
	private IMessageService messageService;
	@Autowired
    private IEnvelopeService envelopeService;
	
	
	@RequestMapping("/inMessageList")
	@ResponseBody
	public Map getInMessageList(@RequestBody Map<String, Object> parameters) {
		Map<String,Object> envelopeMap = new HashMap<String, Object>();
		List<Envelope> envelopeList = envelopeService.findList(parameters);
		envelopeMap.put("data", envelopeList);
		// 获取总记录条数
		int total = PageUtil.getTotalCount();
		envelopeMap.put("total", total != -1 ? total : envelopeList.size());
		
		return envelopeMap;
		
	}
	
	/**
     * 根据主题、发件人、收件人或日期、信封状态查找用户
     *
     * @param map key分别为： 
     *              <code>messageTopic<code>[消息主题]
     *              <code>senderName<code>[发件人]
     *              <code>receiverName<code>[收件人]
     *              <code>receiveState<code>[收件状态]
     *              <code>sendTimeFrom<code>[发送时间]
     *              <code>sendTimeTo<code>[发送时间]
     * @return Map key分别为：
     *              <code>total<code>[总记录条数] 
     *              <code>data<code>[用户信息列表], List 内容为 User
     * 
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map getByParams(@RequestBody Map<String, String> map) {
        Map<String, Object> envelopedata = new HashMap<String, Object>();
        List<Envelope> envelopes = new ArrayList<Envelope>();
        envelopes = envelopeService.getByParams(map);
        envelopedata.put("data", envelopes);
        // 获取总记录条数
        int total = PageUtil.getTotalCount();
        envelopedata.put("total", total != -1 ? total : envelopes.size());
        return envelopedata;
    }
    
    /**
     * 新增、修改用户的保存操作
     * 
     * @param user
     * 
     * @return 用户列表页面路径
     * 
     */
    @RequestMapping(value = "/save")
    public String saveEnvelope(Envelope envelope) {
    	Message ms = messageService.saveMessage(envelope.getMessage());
    	envelope.setMessageId(ms.getId());
    	envelopeService.save(envelope);
        //页面重定向
        return "redirect:/command/mc/core";
    } 
    
    /**
     * 新增、修改用户的异步保存操作
     * 
     * @param message
     * 
     * @return 用户列表页面路径
     * 
     */
    @RequestMapping(value = "/ajaxsave", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> ajaxSave(Envelope envelope) {
    	Envelope en = envelope; 
    	Message me = en.getMessage();
    	messageService.saveMessage(me);
    	en.setMessageId(me.getId());
    	envelopeService.save(en);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);
        return model;
    }

    
    @RequestMapping("/delete/{ids,boxType}")
    public String delete(@PathVariable String ids, String boxType){
    	 if (ids != null) {
             String[] idArray = ids.split(",");
    		 Map<String, String> envelopeMap = new HashMap<String, String>();
             for(int i = 0; i < idArray.length; i++){
            	 envelopeMap.put("id", idArray[i]);
            	 envelopeMap.put("boxType", boxType);
             }
             envelopeService.delete(envelopeMap);;
         }
         return "redirect:/command/mc/core";
    	
    }
    /**
     * 用户修改页面的弹出
     * 
     * @param id [主键ID]
     * 
     * @return Map key为
     *          <code>user<code>[User对象]
     * 
     */
    @RequestMapping("/edit")
    public ModelAndView editPage(@RequestParam(value = "id", required = false) String id) {
        Envelope envelope = null;
        if (id != null && !"".equals(id)) {
        	envelope = envelopeService.findOne(id);
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("envelope", envelope);
        return new ModelAndView("mc/instationmessage/newmessage", model);
    }

}
