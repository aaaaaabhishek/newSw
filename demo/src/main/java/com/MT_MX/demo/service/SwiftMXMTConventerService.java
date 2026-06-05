
package com.MT_MX.demo.service;

import com.MT_MX.demo.iso20022.head_001_001_02.BusinessApplicationHeaderV02;
import com.MT_MX.demo.iso20022.pacs.*;
import com.MT_MX.demo.iso20022.wrapper.MxEnvelope;
import com.MT_MX.demo.utils.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.xml.transform.stream.StreamSource;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Service
public class SwiftMXMTConventerService {

    private final String proccedDir = "E:\\aa\\mt\\convert_mt";

    public String convert(MultipartFile file) {
        try {
            // Read MX XML
            String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            String normalizedXml = MxNormalizer.normalize(xml);

            // Unmarshal MX XML to JAXB objects
            JAXBContext jaxbContext = JAXBContext.newInstance(MxEnvelope.class,
                    Document.class,
                    com.MT_MX.demo.iso20022.pacs_009_001_12.Document.class
            );
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            JAXBElement<MxEnvelope> root = unmarshaller.unmarshal(
                    new StreamSource(new StringReader(normalizedXml)),
                    MxEnvelope.class
            );

            MxEnvelope envelope = root.getValue();
            BusinessApplicationHeaderV02 header = envelope.getAppHdr();
//            Document document = envelope.getDocument();
            Object document=envelope.getDocument();
            if (document instanceof jakarta.xml.bind.JAXBElement<?> jaxb) {
                document = jaxb.getValue();
            }
            System.out.println("Document class = " + document.getClass());
            String mtText=null;
            // Optional: validate MX
            String mxType = SwiftMessageUtils.detectMxType(xml);
           System.out.println("mx="+mxType);
            if("pacs.008".equals(mxType)){
                if (document instanceof Document pacs008Doc) {

                    mtText = MT103Builder.buildMT103FromDocument(pacs008Doc);

                }
                else if (document instanceof org.w3c.dom.Element element) {
                    JAXBContext ctx = JAXBContext.newInstance(Document.class);

                   Document pacs008Doc =
                            (Document)
                                    ctx.createUnmarshaller().unmarshal(element);

                    mtText = MT103Builder.buildMT103FromDocument(pacs008Doc);

                } else {
                    throw new RuntimeException("Unsupported document type: " + document.getClass());
                }
            }

            else if ("pacs.009".equals(mxType)) {
             boolean isCov=false;
                if(document instanceof com.MT_MX.demo.iso20022.pacs_009_001_12.Document pacs009Doc){
                    isCov=SwiftMessageUtils.isCover(header, pacs009Doc );
                    System.out.println("iscov"+isCov);
                    if(!isCov) {
                        mtText = MT202Builder.buildMT202FromDocument(pacs009Doc);
                    }else{
                        mtText = MT202CovBuilder.buildMT202COVFromDocument(pacs009Doc);
                    }

                }
                else if (document instanceof org.w3c.dom.Element element) {
                    JAXBContext ctx = JAXBContext.newInstance(com.MT_MX.demo.iso20022.pacs_009_001_12.Document.class);

                    com.MT_MX.demo.iso20022.pacs_009_001_12.Document pacs009Doc =
                            (com.MT_MX.demo.iso20022.pacs_009_001_12.Document)
                                    ctx.createUnmarshaller().unmarshal(element);

                    if(!isCov) {
                        mtText = MT202Builder.buildMT202FromDocument(pacs009Doc);
                    }else{
                        mtText = MT202CovBuilder.buildMT202COVFromDocument(pacs009Doc);
                    }
                } else {
                    throw new RuntimeException("Unsupported document type: " + document.getClass());
                }
            }
            // MXValidator validator = resolveValidator(mxType);
            // validator.validate(document);

            // Save to file
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Path path = Paths.get(proccedDir, "MT_" + ts + ".txt");
            writeToFile(mtText, path.toString());

            return "Success → " + path;

        } catch (Exception e) {
            throw new RuntimeException("MX → MT conversion failed", e);
        }
    }
    private String detectMxType(Document document) {
        if (document.getFIToFICstmrCdtTrf() != null) return "pacs.008";
//        else if (document.getFIToFICstmrCdtTrf()!=) {
//
//        }
        throw new RuntimeException("Unsupported MX type");
    }

    private void writeToFile(String content, String path) {
        try (FileWriter w = new FileWriter(path)) {
            w.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
