package com.MT_MX.demo.service;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.entity.TransactionHash;
import com.MT_MX.demo.iso20022.head_001_001_02.*;
import com.MT_MX.demo.repository.DuplicateCheckSHARepository;
import com.MT_MX.demo.semantic.parser.*;
import com.MT_MX.demo.utils.*;
import com.MT_MX.demo.validator.MT103Validator;
import com.MT_MX.demo.validator.MT202CovValidator;
import com.MT_MX.demo.validator.MT202Validator;
import com.MT_MX.demo.validator.MTValidator;

import com.MT_MX.demo.iso20022.pacs.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.ValidationEventHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Service
public class SwiftMTMXConventerService {

    private final DuplicateCheckSHARepository duplicateCheckSHARepository;
    private final MT103Validator mt103Validator;
    private final MT202Validator mt202Validator;
    private final MT202CovValidator mt202CovValidator;
    private final SwiftAstBuilder builder = new SwiftAstBuilder();
    private final String processedDir = "E:\\aa\\mt\\convert_mx";

    @Value("${swift.cbpr.bizSvc}")
    private String bizSvc;
    private final MtToMxDocumentMapperFactory mtToMxDocumentMapperFactory;
    public SwiftMTMXConventerService(DuplicateCheckSHARepository duplicateCheckSHARepository, MT103Validator mt103Validator, MT202Validator mt202Validator, MT202CovValidator mt202CovValidator, MtToMxDocumentMapperFactory mtToMxDocumentMapperFactory) {
        this.duplicateCheckSHARepository = duplicateCheckSHARepository;
        this.mt103Validator = mt103Validator;
        this.mt202Validator = mt202Validator;
        this.mt202CovValidator = mt202CovValidator;
        this.mtToMxDocumentMapperFactory = mtToMxDocumentMapperFactory;
    }

    String mtType = null;

    public String convert(MultipartFile file) {

        try {
            String rawMessage = new String(file.getBytes(), StandardCharsets.UTF_8);
            // Build AST
            SwiftAst ast = builder.buildAst(rawMessage);

            // Detect MT Type
            mtType = SwiftMessageUtils.detectMtType(ast);
            System.out.println("mttype"+mtType);
            // Validate
            MTValidator validator = resolveValidator(mtType);
            validator.validate(ast);

//            MxDocument document = mtToMxDocumentMapperFactory.map(mtType, ast);
//            com.MT_MX.demo.iso20022.pacs.Document document108=null;
//            com.MT_MX.demo.iso20022.pacs_009_001_12.Document document202=null;
//            if (document instanceof com.MT_MX.demo.iso20022.pacs.Document) {
//                document108 = (com.MT_MX.demo.iso20022.pacs.Document) document;
//            }
//            else if (document instanceof com.MT_MX.demo.iso20022.pacs_009_001_12.Document) {
//                document202 = (com.MT_MX.demo.iso20022.pacs_009_001_12.Document) document;
//            }
            String ts = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            //Duplictae check
            String ref20 = ast.getField("20").getValue();
            String field32A = ast.getField("32A").getValue();

            // Example parsing
            String valueDate1 = field32A.substring(0, 6);
            String currency1 = field32A.substring(6, 9);
            String amount1 = field32A.substring(9);
            // 5️⃣ Build business key
            String businessKey =
                    "senderBic" + "|" +
                            "receiverBic" + "|" +
                            ref20 + "|" +
                            mtType + "|" +
                            valueDate1 + "|" +
                            currency1 + "|" +
                            amount1;
            String hash = "";
            try {
                hash = HashGeneratorUtil.generateHash(businessKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isDuplicate = duplicateCheckSHARepository
                    .existsByHashAndDeletedFalseAndCreatedAtAfter(
                            hash,
                            LocalDateTime.now().minusDays(90)
                    );

            if (!isDuplicate) {

                TransactionHash transactionHash = new TransactionHash();
                transactionHash.setHash(hash);
                transactionHash.setCreatedAt(LocalDateTime.now());
                transactionHash.setDeleted(false);

                duplicateCheckSHARepository.save(transactionHash);
            }
            // Build MX + AppHdr
            com.MT_MX.demo.iso20022.pacs.Document document103=null;
            com.MT_MX.demo.iso20022.pacs_009_001_12.Document document202=null;
            switch (mtType) {

                case "103":
                    document103 = DocumentUtil.buildMX(ast);
                    break;

                case "202":
                    document202 = Document202.buildMX(ast);
                    break;
                case "202COV":
                    document202=Document202Cov.buildMX(ast);
                default:
                    throw new IllegalArgumentException("Unsupported MT type: " + mtType);
            }
            BusinessApplicationHeaderV02 appHdr = AppHeaderUtil.buildAppHdr(ast, isDuplicate);


            Path outputPath = Paths.get(processedDir,
                    "MX_" + ts + ".txt");

            File outFile = new File(outputPath.toString());
            SchemaFactory sf =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema appHdrSchema = sf.newSchema(
                    new StreamSource(
                            getClass().getClassLoader()
                                    .getResourceAsStream("schema/head.001.001.02.xsd")
                    )
            );
            Schema docSchema=null;
            if(document202==null) {
                docSchema = sf.newSchema(
                        new StreamSource(
                                getClass().getClassLoader()
                                        .getResourceAsStream("schema/pacs.008.001.13.xsd")
                        )
                );
            }else{
                docSchema = sf.newSchema(
                        new StreamSource(
                                getClass().getClassLoader()
                                        .getResourceAsStream("schema/pacs.009.001.12.xsd")
                        )
                );
            }
            ValidationEventHandler handler = event -> {
                System.out.println("XSD Validation Error: " + event.getMessage());
                return false;
            };
            try (FileOutputStream fos = new FileOutputStream(outFile)) {

                // AppHdr marshaller
                JAXBContext hdrCtx = JAXBContext.newInstance(BusinessApplicationHeaderV02.class);
                Marshaller hdrMarshaller = hdrCtx.createMarshaller();
                hdrMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                hdrMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                hdrMarshaller.setSchema(appHdrSchema);
                QName appHdrQName = new QName(
                        "urn:iso:std:iso:20022:tech:xsd:head.001.001.02",
                        "AppHdr",
                        "head"
                );

                JAXBElement<BusinessApplicationHeaderV02> appHdrElement =
                        new JAXBElement<>(appHdrQName, BusinessApplicationHeaderV02.class, appHdr);

                hdrMarshaller.marshal(appHdrElement, fos);

                fos.write("\n".getBytes());


                // Document marshaller
                Marshaller docMarshaller = null;

                if (document103 != null) {

                    JAXBContext docCtx = JAXBContext.newInstance(
                            com.MT_MX.demo.iso20022.pacs.Document.class
                    );

                    docMarshaller = docCtx.createMarshaller();
                    docMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    docMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    docMarshaller.setSchema(docSchema);
                    docMarshaller.setEventHandler(handler);

                    QName docQName = new QName(
                            "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.13",
                            "Document"
                    );

                    JAXBElement<com.MT_MX.demo.iso20022.pacs.Document> docElement108 =
                            new JAXBElement<>(docQName,
                                    com.MT_MX.demo.iso20022.pacs.Document.class,
                                    document103);

                    docMarshaller.marshal(docElement108, fos);

                } else if (document202 != null) {

                    JAXBContext docCtx = JAXBContext.newInstance(
                            com.MT_MX.demo.iso20022.pacs_009_001_12.Document.class
                    );

                    docMarshaller = docCtx.createMarshaller();
                    docMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    docMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    docMarshaller.setSchema(docSchema);
                    docMarshaller.setEventHandler(handler);

                    QName docQName = new QName(
                            "urn:iso:std:iso:20022:tech:xsd:pacs.009.001.12",
                            "Document"
                    );

                    JAXBElement<com.MT_MX.demo.iso20022.pacs_009_001_12.Document> docElement202 =
                            new JAXBElement<>(docQName,
                                    com.MT_MX.demo.iso20022.pacs_009_001_12.Document.class,
                                    document202);

                    docMarshaller.marshal(docElement202, fos);
                }
            }
            return "SUCCESS → " + outputPath;

        } catch (Exception e) {
            throw new RuntimeException("MT → MX Conversion Failed", e);
        }
    }
    // ==========================================================
    // MT Type Validator Resolver
    // ==========================================================
    private MTValidator resolveValidator(String mtType) {
        if ("103".equals(mtType))
            return mt103Validator;
        else if("202".equals(mtType)){
            return mt202Validator;
        }else if("202COV".equals(mtType)){
            return mt202CovValidator;
        }

        throw new RuntimeException("Unsupported MT Type: " + mtType);
    }
}


