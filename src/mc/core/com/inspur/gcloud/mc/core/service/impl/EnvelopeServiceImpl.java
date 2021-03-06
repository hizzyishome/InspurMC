package com.inspur.gcloud.mc.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.inspur.gcloud.mc.core.dao.EnvelopeDao;
import com.inspur.gcloud.mc.core.dao.MessageDao;
import com.inspur.gcloud.mc.core.data.Envelope;
import com.inspur.gcloud.mc.core.data.Message;
import com.inspur.gcloud.mc.core.service.IEnvelopeService;
import com.lc.gcloud.framework.util.GCloudUtil;

@Service("envelopeService")
public class EnvelopeServiceImpl implements IEnvelopeService {
	
	@Autowired
	private EnvelopeDao envelopeDao;
	@Autowired
	private MessageDao messageDao;
	
	@Resource(name = "transactionTemplate")
	private TransactionTemplate transactionTemplate;
	
	@Override
	public List<Envelope> findEnvelopeListByMessageId(String messageId) {
		return envelopeDao.findEnvelopeListByMessageId(messageId);
	}
	
	@Override
	public Envelope findEnvelopeByMessageIdAndLoginId(Map<String, String> map) {
		Envelope envelope = envelopeDao.findEnvelopeByMessageIdAndLoginId(map);
		return envelope;
	}
	
	@Override
	public List<Envelope> findEnvelopeListByMessageIdAndLoginId(Map<String, String> map) {
		return envelopeDao.findEnvelopeListByMessageIdAndLoginId(map);
	}

	@Override
	public List<Envelope> findList(Map<String, String> parameters) {
		return envelopeDao.findList(parameters);
	}

	@Override
	public List<Envelope> getByParams(Map<String, String> parameters) {
		return envelopeDao.getByParams(parameters);
	}

	@Override
	public Envelope findEnvelopeById(String id) {
		return envelopeDao.get(id);
	}
	
	@Override
	public Message findMessage(String id) {
		return messageDao.get(id);
	}
	// 单条保存
	@Transactional
	public int saveEnvelope(Envelope envelope, String messageId) {
		int count = 0;
		if (envelope.getId() != null && !envelope.getId().equals("")) {
            // 更新信封信息
        	count = envelopeDao.update(envelope);
        } else {
            // 保存信封信息
        	envelope.setId(GCloudUtil.getInstance().getNextSeqId(32));
        	envelope.setMessageId(messageId);
        	count = envelopeDao.insert(envelope);
        }
        return count;
	}
	
	@Transactional
	public void delete(Map<String, Object> map) {

		@SuppressWarnings("unchecked")
		List<Envelope> envelopeList = (List<Envelope>) map.get("envelopeList");
		for(int i = 0; i < envelopeList.size(); i++) {
			Map<String, String> changeMap = new HashMap<String, String>();
			changeMap.put("id", envelopeList.get(i).getId());
			changeMap.put("boxType", (String) map.get("boxType"));
			envelopeDao.changeState(changeMap);
		}
	}
	
	//物理删除：用于批量更新前删除
	@Transactional
	public void physicalDelete(List<Envelope> envelopeList, String messageId) {
		//删除消息
		messageDao.delete(messageId);
		//删除信封
		String[] envelopeIds = new String[envelopeList.size()];
		for (int i = 0; i < envelopeList.size(); i++) {
			envelopeIds[i] = envelopeList.get(i).getId();
		}
		envelopeDao.batchDelete(envelopeIds);
	}
	
	//插入新信封
	public Boolean batchInsertEnvelope(List<Envelope> envelopeList, Message message) {
		List<Envelope> newEnvelopeList = new ArrayList<Envelope>();
		message.setId(GCloudUtil.getInstance().getNextSeqId(32));
		for(int i = 0; i < envelopeList.size(); i++){
			Envelope envelope = envelopeList.get(i);
			envelope.setId(GCloudUtil.getInstance().getNextSeqId(32));
			envelope.setMessageId(message.getId());
			envelope.setMessage(message);
			newEnvelopeList.add(envelope);
		}
		messageDao.insert(message);
		envelopeDao.batchInsert(newEnvelopeList);
		return true;
	}

	@Override
	public String findMessageId(String id) {
		return envelopeDao.getMessageId(id);
	}

	@Override
	public int updateEnvelope(Envelope envelope) {
		return envelopeDao.update(envelope);
	}



}
