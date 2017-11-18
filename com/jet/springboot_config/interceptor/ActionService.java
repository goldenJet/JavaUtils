package com.jet.springboot_config.interceptor;

import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by jet.chen on 2017/2/24.
 */
@Service
@Log4j
public class ActionService {

    private Map<String, Action> actionMap;

    private Map<String, ActionModel> actionModelMap;

    @Autowired
    public ActionService(Environment environment) throws IOException, DocumentException, URISyntaxException {
        actionMap = new TreeMap<>();
        actionModelMap = new TreeMap<>();
        List<String> scanLocaleRootList = Arrays.asList(environment.getProperty("action.locations").split(","));
        reloadActionService(scanLocaleRootList);
    }

    private boolean checkFileIsConfigureAction(String name){
        if (StringUtils.getFilenameExtension(name) != null &&
                StringUtils.getFilenameExtension(name).toLowerCase().equals("xml")) {
            return true;
        }
        return false;
    }

    public void reloadActionService(List<String> scanLocaleRootList) throws IOException, DocumentException, URISyntaxException {
        List<InputStream> fileDirectoryList = new ArrayList<>();
        for (String localeRoot : scanLocaleRootList) {
            if (StringUtils.isEmpty(localeRoot)) {
                continue;
            }
            //根据类路径获取
            if (localeRoot.startsWith("classpath:")) {
                localeRoot = localeRoot.substring("classpath:".length(), localeRoot.length());
//                FileInputStream directorInputStream =  (FileInputStream) this.getClass().getClassLoader().getResourceAsStream(localeRoot);
                URL url = this.getClass().getClassLoader().getResource(localeRoot);
                String protocol = url.getProtocol();
                log.info("load classpath: <" + localeRoot + "> for action config! protocol:" + url.getProtocol());
                if (url != null) {
                    if("file".equalsIgnoreCase(protocol)){
                        File file = new File(url.getFile());
                        if (file.isDirectory()) {
                            File[] files = file.listFiles(pathname -> checkFileIsConfigureAction(pathname.getName()));
                            for(File file1:files){
                                log.info("reloadActionService FILE:" + file.getName());
                                fileDirectoryList.add(new FileInputStream(file1));
                            }
                        }
                    }else if("jar".equalsIgnoreCase(protocol)){
                        JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                        while(entryEnumeration.hasMoreElements()){
                            JarEntry jarEntry = entryEnumeration.nextElement();
                            log.info("reloadActionService JAR:" + jarEntry.getName());
                            if(checkFileIsConfigureAction(jarEntry.getName())){
                                fileDirectoryList.add(jarFile.getInputStream(jarEntry));
                            }
                        }
                    }
                }
            }
            //根据文件路径获取
            else {
                File file = new File(localeRoot);
                if (file.isDirectory()) {
                   File[] files = file.listFiles(pathname -> checkFileIsConfigureAction(pathname.getName()));
                    for(File file1:files){
                        log.info("reloadActionService FILE:" + file.getName());
                        fileDirectoryList.add(new FileInputStream(file1));
                    }
                }
            }
            readActionConfigFile(fileDirectoryList);
        }
    }

    public ActionModel searchActionModelByName(String name) {
        return actionModelMap.get(name);
    }

    public Action searchActionByNameAndType(String name, String type) {
        return actionMap.get(name + ":" + type);
    }

    public Action searchActionByAction(Action action) {
        return searchActionByNameAndType(action.getName(), action.getType());
    }

    public Collection<Action> listAllAction() {
        return actionMap.values();
    }

    public List<Action> searchActionByCondition(QueryCondition... queryConditionList) throws IllegalAccessException {
        List<Action> actionList = new ArrayList<>();
        for (Action action : actionMap.values()) {
            boolean match = true;
            for (QueryCondition queryCondition : queryConditionList) {
                match = matchCondition(action, queryCondition) && match;
            }
            if (match) {
                actionList.add(action);
            }
        }
        return actionList;
    }


    public List<ActionModel> searchActionModelByCondition(QueryCondition... queryConditionList) throws IllegalAccessException {
        List<ActionModel> actionModelList = new ArrayList<>();
        for (ActionModel actionModel : actionModelMap.values()) {
            boolean match = true;
            for (QueryCondition queryCondition : queryConditionList) {
                match = matchCondition(actionModel, queryCondition) && match;
            }
            if (match) {
                actionModelList.add(actionModel);
            }
        }

        return actionModelList;
    }

    private boolean matchCondition(Object object, QueryCondition queryCondition) throws IllegalAccessException {
        boolean result = false;
        QueryCondition.Condition condition = queryCondition.getCondition();
        Object queryValue = queryCondition.getValue();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getName().equals(queryCondition.getAttribute())) {
                field.setAccessible(true);
                Object attributeValue = field.get(object);
                switch (condition) {
                    case EQUAL:
                        if (attributeValue != null) {
                            result = attributeValue.equals(queryValue);
                        } else {
                            result = attributeValue == queryValue;
                        }
                        break;
                    case UNEQUAL:
                        if (attributeValue != null) {
                            result = !attributeValue.equals(queryValue);
                        } else {
                            result = attributeValue != queryValue;
                        }
                        break;
                    case LIKE:
                        if (attributeValue != null && attributeValue instanceof String && queryValue instanceof String) {
                            String attributeStringValue = (String) attributeValue;
                            String queryStringValue = (String) queryValue;
                            result = attributeStringValue.contains(queryStringValue);
                        } else {
                            result = false;
                        }
                        break;
                    case UNLIKE:
                        if (attributeValue != null && attributeValue instanceof String && queryValue instanceof String) {
                            String attributeStringValue = (String) attributeValue;
                            String queryStringValue = (String) queryValue;
                            result = !attributeStringValue.contains(queryStringValue);
                        } else {
                            result = true;
                        }
                        break;
                    default:
                        result = false;
                }
            }
        }
        return result;
    }

    private void readActionConfigFile(List<InputStream> files) throws DocumentException {
        for (InputStream file : files) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            String rootName = rootElement.getName();
            if (rootName.equals("actionmodels")) {
                praseActionModels(rootElement);
            } else if (rootName.equals("listofactions")) {
                praseActions(rootElement);
            }
        }
    }


    private void praseActionModels(Element element) {
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element subElement = (Element) iterator.next();
            ActionModel actionModel = new ActionModel(subElement);
            log.info(actionModel);
            actionModelMap.put(actionModel.getName(), actionModel);
        }
    }

    private void praseActions(Element element) {
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element objectTypeElement = (Element) iterator.next();
            String objectResourceBundle = objectTypeElement.attributeValue("resourceBundle");
            Iterator actionIterator = objectTypeElement.elementIterator();
            while (actionIterator.hasNext()) {
                Element actionElement = (Element) actionIterator.next();
                Action action = new Action(actionElement);
                if (action.getResourceBundle() == null) {
                    action.setResourceBundle(objectResourceBundle);
                }
                actionMap.put(action.getName() + ":" + action.getType(), action);
            }
        }
    }


}
