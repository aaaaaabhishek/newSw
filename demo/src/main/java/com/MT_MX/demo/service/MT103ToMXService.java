package com.MT_MX.demo.service;

import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.semantic.BeneficiaryCustomer;
import com.MT_MX.demo.semantic.Field32AValue;
import com.MT_MX.demo.semantic.OrderingCustomer;
import com.MT_MX.demo.semantic.parser.Field32AParser;
import com.MT_MX.demo.semantic.parser.Field50KParser;
import com.MT_MX.demo.semantic.parser.Field59Parser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MT103ToMXService {

    public Map<String, Object> convertAstToMXMap(SwiftAst ast) {

        Map<String, Object> data = new HashMap<>();

        BlockNode b1 = ast.getBlock(1);
        BlockNode b2 = ast.getBlock(2);
        BlockNode b4 = ast.getBlock(4);

        // -------------------------
        // Header
        // -------------------------
        data.put("senderBic", safeSubstring(b1, 5, 16));
        data.put("receiverBic", safeSubstring(b2, 4, 15));
        data.put("msgId", generateMessageId());
        data.put("creationDateTime", OffsetDateTime.now().toString());

        if (b4 == null) return data;

        // -------------------------
        // Debtor — semantic
        // -------------------------
        OrderingCustomer debtor =
                Field50KParser.parse(b4.getField("50K"));

        if (debtor != null) {
            Map<String,Object> d = new HashMap<>();
            d.put("iban", debtor.getAccount());
            d.put("name", debtor.getName());
            d.put("address", debtor.getAddress());
            data.put("debtor", d);
        }

        // -------------------------
        // Creditor — semantic
        // -------------------------
        BeneficiaryCustomer creditor =
                Field59Parser.parse(b4.getField("59"));

        if (creditor != null) {
            Map<String,Object> c = new HashMap<>();
            c.put("iban", creditor.getAccount());
            c.put("nameAddress", creditor.getAddress());
            data.put("creditor", c);
        }

        // -------------------------
        // Amount — semantic
        // -------------------------
        Field32AValue v32 =
                Field32AParser.parse(b4.getField("32A"));

        if (v32 != null) {
            data.put("settlementDate", v32.getValueDate().toString());
            System.out.println(v32.getCurrency());
            data.put("currency", v32.getCurrency());
            data.put("amount", v32.getAmount().toPlainString());
        }

        // -------------------------
        // Simple fields
        // -------------------------
        data.put("chargeBearer", b4.getFieldValue("71A"));
        data.put("remittanceInfo", b4.getFieldValue("70"));

        return data;
    }

    private String safeSubstring(BlockNode b, int start, int end) {
        if (b == null || b.getContent() == null) return "";
        String c = b.getContent();
        if (c.length() < end) return "";
        return c.substring(start, end);
    }

    private String generateMessageId() {
        return "MSG" + System.currentTimeMillis();
    }
}

//package com.MT_MX.demo.service;
//import com.MT_MX.demo.ast.SwiftAst;
//import com.MT_MX.demo.ast.FieldNode;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class MT103ToMXService {
//
//    public Map<String, Object> convertAstToMXMap(SwiftAst ast) {
//        Map<String, Object> data = new HashMap<>();
//
//        // Header info
//        data.put("senderBic", ast.getBlock(1).getContent().substring(5, 16));
//        data.put("receiverBic", ast.getBlock(2).getContent().substring(4, 15));
//        data.put("msgId", "MSG" + System.currentTimeMillis());
//        data.put("creationDateTime", java.time.OffsetDateTime.now().toString());
//
//        // Example mapping: Debtor
//        FieldNode field50K = ast.getBlock(4).getField("50K");
//        Map<String, Object> debtor = new HashMap<>();
//        debtor.put("name", field50K != null ? field50K.getLines().get(1) : "");
//        debtor.put("iban", field50K != null ? field50K.getLines().get(0).substring(1) : "");
//        debtor.put("address", field50K != null ? field50K.getLines().subList(1, field50K.getLines().size()) : List.of());
//        data.put("debtor", debtor);
//
//        // Creditor
//        FieldNode field59 = ast.getBlock(4).getField("59");
//        Map<String, Object> creditor = new HashMap<>();
//        creditor.put("name", field59 != null ? field59.getLines().get(1) : "");
//        creditor.put("iban", field59 != null ? field59.getLines().get(0).substring(1) : "");
//        creditor.put("address", field59 != null ? field59.getLines().subList(1, field59.getLines().size()) : List.of());
//        data.put("creditor", creditor);
//
//        // Amount, currency, date
//        FieldNode field32A = ast.getBlock(4).getField("32A");
//        if (field32A != null) {
//            String value = field32A.getLines().get(0);
//            data.put("currency", value.substring(6, 9));
//            data.put("amount", value.substring(9));
//            data.put("settlementDate", value.substring(0, 6));
//        }
//
//        // Charges and remittance
//        data.put("chargeBearer", ast.getBlock(4).getFieldValue("71A"));
//        data.put("remittanceInfo", ast.getBlock(4).getFieldValue("70"));
//
//        return data;
//    }
//    private String generateMessageId() {
//        return "MSG" + System.currentTimeMillis(); // or any unique ID generation logic
//    }
//
//}
