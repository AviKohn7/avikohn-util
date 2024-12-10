package dev.avikohn.util.testutil.params;

import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class XmlQueryReader{
    private static boolean DEFAULT_TRIM = false;
    private String pathName;
    private boolean trim = DEFAULT_TRIM;
    public XmlQueryReader(String pathName){
        this.pathName = pathName;
    }
    public Stream<Object[]> readXml(int argumentCount) throws IOException, ParserConfigurationException, SAXException {
        Element queryParent = getQueryParent();
        NodeList queries = queryParent.getElementsByTagName("query");
        List<Object[]> queryList = new ArrayList<Object[]>();

        for(int i = 0; i < queries.getLength(); i++){
            Node query = queries.item(i);
            if(query.getParentNode() != queryParent) continue;
            queryList.add(buildQuery(query, argumentCount));
        }
        trim = DEFAULT_TRIM;
        return queryList.stream();
    }
    private Element getQueryParent() throws IOException, ParserConfigurationException, SAXException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(pathName));
        Element rootElement = document.getDocumentElement();
        this.trim = getTrimValue(rootElement);
        return rootElement;
    }
    private Object[] buildQuery(Node query, int argumentCount){
        NodeList children = query.getChildNodes();
        List<Object> list = new ArrayList<>();
        boolean trim = getTrimValue(query);
        for(int i = 0; i < children.getLength(); i++){
            Node queryChild = children.item(i);
            if(queryChild.getNodeType() == Node.ELEMENT_NODE){
                list.add(getNodeContent(queryChild, trim));
            }
        }
        if(list.size() < argumentCount) {
            throw new ParameterResolutionException(String.format("XML file %s contains a query with too few arguments for the selected test. The test required %d arguments, found %d",
            pathName, argumentCount, list.size()));
        }
        return list.toArray();
    }
    private Object getNodeContent(Node node, boolean currentTrimValue){
        boolean trim = getTrimValue(node, currentTrimValue);
        String value = node.getTextContent();
        if(trim) value = value.trim();
        return parseType(node, value);
    }
    private Object parseType(Node node, String content){
        if(node instanceof Element elem){
            if(attributeEqualsInsensitive(elem, "type", "integer", "int")){
                return Integer.parseInt(content.trim()); //trim by default if numeric
            } else if(attributeEqualsInsensitive(elem, "type", "double")){
                return Double.parseDouble(content.trim());
            } else if(attributeEqualsInsensitive(elem, "type", "boolean", "bool")){
                return Boolean.parseBoolean(content.trim());
            }
        }
        return content;
    }
    boolean getTrimValue(Node query){
        return getTrimValue(query, this.trim);
    }
    boolean getTrimValue(Node node, boolean currTrimValue){
        return triStateHandler(attributeEqualsInsensitive(node, "trim", "true"),
                attributeEqualsInsensitive(node, "trim", "false"),
                currTrimValue);
    }
    private boolean triStateHandler(boolean yes, boolean not, boolean fallback){
        if(yes || not) return yes; //if yes is false, that is returning !not
        else return fallback;
    }
    private boolean attributeEqualsInsensitive(Node node, String attribute, String... values){
        return node instanceof Element elem &&
                Arrays.asList(values).contains(elem.getAttribute(attribute).toLowerCase());
    }
}
