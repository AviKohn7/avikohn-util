package dev.avikohn.util.testutil.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.stream.Stream;

class XMLArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<XMLSource>{

    private String fileName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        try {
            return getArgsStream(context);
        } catch(IOException e){
            throw new IllegalArgumentException("Failed to load test arguments, can't find file \"" + fileName + "\"", e);
        } catch(SAXException | ParserConfigurationException e){
            throw new IllegalArgumentException("Failed to parse XML file \"" + fileName + "\"", e);
        }
    }
    private Stream<? extends Arguments> getArgsStream(ExtensionContext context) throws IOException, ParserConfigurationException, SAXException{
        int paramCount = context.getRequiredTestMethod().getParameterCount();
        XmlQueryReader reader = new XmlQueryReader(fileName);
        Stream<Object[]> queries = reader.readXml(paramCount);
        return queries.map(Arguments::of);
    }
    @Override
    public void accept(XMLSource variableSource) {
        fileName = variableSource.value();
    }
}
