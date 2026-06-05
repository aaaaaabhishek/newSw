package com.MT_MX.demo.validator;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.MxElementNode;
import com.MT_MX.demo.ast.SwiftMxAst;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class pacs008Validator implements MXValidator {

    private static final List<String> MANDATORY_FIELDS = List.of(
            "FIToFICstmrCdtTrf/GrpHdr/MsgId",
            "FIToFICstmrCdtTrf/GrpHdr/CreDtTm",
            "FIToFICstmrCdtTrf/GrpHdr/NbOfTxs",
            "FIToFICstmrCdtTrf/CdtTrfTxInf/PmtId/EndToEndId",
            "FIToFICstmrCdtTrf/CdtTrfTxInf/IntrBkSttlmAmt",
            "FIToFICstmrCdtTrf/CdtTrfTxInf/Dbtr/Nm",
            "FIToFICstmrCdtTrf/CdtTrfTxInf/Cdtr/Nm"
    );
    @Override
    public void validate(SwiftMxAst ast) {

        List<ValidationError> errors = new ArrayList<>();

        // =========================
        // Mandatory checks
        // =========================

        for (String path : MANDATORY_FIELDS) {
            if (ast.getValue(path) == null) {
                errors.add(new ValidationError(
                        path, "MX01", "Mandatory MX element missing"
                ));
            }
        }

        // =========================
        // Format checks
        // =========================

        checkCreationDateTime(ast, errors);
        checkNbOfTxs(ast, errors);
        checkSettlementAmount(ast, errors);
        checkCurrency(ast, errors);

        // =========================
        // Business checks
        // =========================

        checkNbOfTxsConsistency(ast, errors);

        // =========================
        // Result
        // =========================

        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            throw new RuntimeException("pacs.008 validation failed");
        }

        System.out.println("pacs.008 validation passed");
    }

    // =====================================================
    // FORMAT
    // =====================================================

    private void checkCreationDateTime(
            SwiftMxAst ast,
            List<ValidationError> errors) {

        String v = ast.getValue(
                "Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm");

        if (v == null) return;

        try {
            OffsetDateTime.parse(v);
        } catch (Exception e) {
            errors.add(new ValidationError(
                    "CreDtTm",
                    "MXFMT01",
                    "Invalid ISO datetime"
            ));
        }
    }

    private void checkNbOfTxs(
            SwiftMxAst ast,
            List<ValidationError> errors) {

        String v = ast.getValue(
                "Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs");

        if (v == null) return;

        try {
            int n = Integer.parseInt(v);
            if (n <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            errors.add(new ValidationError(
                    "NbOfTxs",
                    "MXFMT02",
                    "NbOfTxs must be positive integer"
            ));
        }
    }

    private void checkSettlementAmount(
            SwiftMxAst ast,
            List<ValidationError> errors) {

        String v = ast.getValue(
                "Document/FIToFICstmrCdtTrf/CdtTrfTxInf/IntrBkSttlmAmt");

        if (v == null) return;

        try {
            BigDecimal amt = new BigDecimal(v);

            if (amt.scale() > 2) {
                errors.add(new ValidationError(
                        "IntrBkSttlmAmt",
                        "MXFMT03",
                        "Amount scale > 2"
                ));
            }

            if (amt.signum() <= 0) {
                errors.add(new ValidationError(
                        "IntrBkSttlmAmt",
                        "MXFMT04",
                        "Amount must be > 0"
                ));
            }

        } catch (Exception e) {
            errors.add(new ValidationError(
                    "IntrBkSttlmAmt",
                    "MXFMT05",
                    "Invalid amount"
            ));
        }
    }

    private void checkCurrency(
            SwiftMxAst ast,
            List<ValidationError> errors) {

        MxElementNode root = ast.getRoot();

        if (root == null) return;

        MxElementNode fi = root.getChild("FIToFICstmrCdtTrf");
        if (fi == null) return;

        MxElementNode tx = fi.getChild("CdtTrfTxInf");
        if (tx == null) return;

        MxElementNode amt = tx.getChild("IntrBkSttlmAmt");
        if (amt == null) return;

        String ccy = amt.getAttributes().get("Ccy");

        if (ccy == null ||
                ccy.length() != 3 ||
                !ccy.chars().allMatch(Character::isLetter)) {

            errors.add(new ValidationError(
                    "Ccy",
                    "MXFMT06",
                    "Invalid currency code"
            ));
        }
    }    // =====================================================
    // BUSINESS
    // =====================================================

    private void checkNbOfTxsConsistency(
            SwiftMxAst ast,
            List<ValidationError> errors) {

        String declaredStr =
                ast.getValue("Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs");

        if (declaredStr == null) return;

        int declared = Integer.parseInt(declaredStr);

        List<MxElementNode> txs =
                ast.getRoot()
                        .getChild("Document")
                        .getChild("FIToFICstmrCdtTrf")
                        .getChildren("CdtTrfTxInf");

        int actual = txs.size();

        if (declared != actual) {
            errors.add(new ValidationError(
                    "NbOfTxs",
                    "MXBUS01",
                    "NbOfTxs does not match transaction count"
            ));
        }
    }
}
//package com.MT_MX.demo.validator;
//
//import com.MT_MX.demo.Exception.ValidationError;
//import com.MT_MX.demo.ast.FieldNode;
//import com.MT_MX.demo.ast.SwiftAst;
//import com.MT_MX.demo.ast.SwiftMxAst;
//
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class pacs008Validator implements MXValidator {
//
//    private static final List<String> MANDATORY_FIELDS = List.of(
//            "GrpHdr.MsgId",
//            "GrpHdr.CreDtTm",
//            "GrpHdr.NbOfTxs",
//            "CdtTrfTxInf.PmtId.EndToEndId",
//            "CdtTrfTxInf.IntrBkSttlmAmt",
//            "CdtTrfTxInf.Dbtr.Nm",
//            "CdtTrfTxInf.Cdtr.Nm"
//    );
//
//    @Override
//    public void validate(SwiftMxAst ast) {
//
//        List<ValidationError> errors = new ArrayList<>();
//
//        // =========================
//        // Mandatory fields
//        // =========================
//        for (String path : MANDATORY_FIELDS) {
//            if (ast.getField(path) == null) {
//                errors.add(new ValidationError(
//                        path,
//                        "MX01",
//                        "Mandatory MX element missing"
//                ));
//            }
//        }
//
//        // =========================
//        // Format validations
//        // =========================
//        checkCreationDateTime(ast, errors);
//        checkNbOfTxs(ast, errors);
//        checkSettlementAmount(ast, errors);
//        checkCurrency(ast, errors);
//
//        // =========================
//        // Business rules
//        // =========================
//        checkNbOfTxsConsistency(ast, errors);
//
//        // =========================
//        // Final result
//        // =========================
//        if (!errors.isEmpty()) {
//            errors.forEach(System.out::println);
//            throw new RuntimeException("pacs.008 validation failed");
//        }
//
//        System.out.println("pacs.008 validation passed.");
//    }
//
//    // =====================================================
//    // FORMAT CHECKS
//    // =====================================================
//
//    private void checkCreationDateTime(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("GrpHdr.CreDtTm");
//        if (f == null) return;
//
//        try {
//            OffsetDateTime.parse(f.getValue());
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "GrpHdr.CreDtTm",
//                    "MXFMT01",
//                    "Invalid ISO datetime format"
//            ));
//        }
//    }
//
//    private void checkNbOfTxs(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("GrpHdr.NbOfTxs");
//        if (f == null) return;
//
//        try {
//            int n = Integer.parseInt(f.getValue());
//            if (n <= 0) throw new NumberFormatException();
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "GrpHdr.NbOfTxs",
//                    "MXFMT02",
//                    "NbOfTxs must be positive integer"
//            ));
//        }
//    }
//
//    private void checkSettlementAmount(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("CdtTrfTxInf.IntrBkSttlmAmt");
//        if (f == null) return;
//
//        try {
//            BigDecimal amt = new BigDecimal(f.getValue());
//            if (amt.scale() > 2) {
//                errors.add(new ValidationError(
//                        "IntrBkSttlmAmt",
//                        "MXFMT03",
//                        "Amount scale > 2 decimals"
//                ));
//            }
//            if (amt.signum() <= 0) {
//                errors.add(new ValidationError(
//                        "IntrBkSttlmAmt",
//                        "MXFMT04",
//                        "Amount must be > 0"
//                ));
//            }
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "IntrBkSttlmAmt",
//                    "MXFMT05",
//                    "Invalid decimal amount"
//            ));
//        }
//    }
//
//    private void checkCurrency(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("CdtTrfTxInf.IntrBkSttlmAmt.Ccy");
//        if (f == null) return;
//
//        String ccy = f.getValue();
//        if (ccy.length() != 3 || !ccy.chars().allMatch(Character::isLetter)) {
//            errors.add(new ValidationError(
//                    "Ccy",
//                    "MXFMT06",
//                    "Invalid currency code"
//            ));
//        }
//    }
//
//    // =====================================================
//    // BUSINESS RULES
//    // =====================================================
//
//    private void checkNbOfTxsConsistency(
//            SwiftAst ast,
//            List<ValidationError> errors
//    ) {
//        FieldNode n = ast.getField("GrpHdr.NbOfTxs");
//        if (n == null) return;
//
//        int declared = Integer.parseInt(n.getValue());
//
//        int actual =
//                ast.getFieldsByPrefix("CdtTrfTxInf").size();
//
//        if (declared != actual) {
//            errors.add(new ValidationError(
//                    "GrpHdr.NbOfTxs",
//                    "MXBUS01",
//                    "NbOfTxs does not match transaction count"
//            ));
//        }
//    }
//}package com.MT_MX.demo.validator;
//
//import com.MT_MX.demo.Exception.ValidationError;
//import com.MT_MX.demo.ast.FieldNode;
//import com.MT_MX.demo.ast.SwiftAst;
//import com.MT_MX.demo.ast.SwiftMxAst;
//
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class pacs008Validator implements MXValidator {
//
//    private static final List<String> MANDATORY_FIELDS = List.of(
//            "GrpHdr.MsgId",
//            "GrpHdr.CreDtTm",
//            "GrpHdr.NbOfTxs",
//            "CdtTrfTxInf.PmtId.EndToEndId",
//            "CdtTrfTxInf.IntrBkSttlmAmt",
//            "CdtTrfTxInf.Dbtr.Nm",
//            "CdtTrfTxInf.Cdtr.Nm"
//    );
//
//    @Override
//    public void validate(SwiftMxAst ast) {
//
//        List<ValidationError> errors = new ArrayList<>();
//
//        // =========================
//        // Mandatory fields
//        // =========================
//        for (String path : MANDATORY_FIELDS) {
//            if (ast.getField(path) == null) {
//                errors.add(new ValidationError(
//                        path,
//                        "MX01",
//                        "Mandatory MX element missing"
//                ));
//            }
//        }
//
//        // =========================
//        // Format validations
//        // =========================
//        checkCreationDateTime(ast, errors);
//        checkNbOfTxs(ast, errors);
//        checkSettlementAmount(ast, errors);
//        checkCurrency(ast, errors);
//
//        // =========================
//        // Business rules
//        // =========================
//        checkNbOfTxsConsistency(ast, errors);
//
//        // =========================
//        // Final result
//        // =========================
//        if (!errors.isEmpty()) {
//            errors.forEach(System.out::println);
//            throw new RuntimeException("pacs.008 validation failed");
//        }
//
//        System.out.println("pacs.008 validation passed.");
//    }
//
//    // =====================================================
//    // FORMAT CHECKS
//    // =====================================================
//
//    private void checkCreationDateTime(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("GrpHdr.CreDtTm");
//        if (f == null) return;
//
//        try {
//            OffsetDateTime.parse(f.getValue());
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "GrpHdr.CreDtTm",
//                    "MXFMT01",
//                    "Invalid ISO datetime format"
//            ));
//        }
//    }
//
//    private void checkNbOfTxs(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("GrpHdr.NbOfTxs");
//        if (f == null) return;
//
//        try {
//            int n = Integer.parseInt(f.getValue());
//            if (n <= 0) throw new NumberFormatException();
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "GrpHdr.NbOfTxs",
//                    "MXFMT02",
//                    "NbOfTxs must be positive integer"
//            ));
//        }
//    }
//
//    private void checkSettlementAmount(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("CdtTrfTxInf.IntrBkSttlmAmt");
//        if (f == null) return;
//
//        try {
//            BigDecimal amt = new BigDecimal(f.getValue());
//            if (amt.scale() > 2) {
//                errors.add(new ValidationError(
//                        "IntrBkSttlmAmt",
//                        "MXFMT03",
//                        "Amount scale > 2 decimals"
//                ));
//            }
//            if (amt.signum() <= 0) {
//                errors.add(new ValidationError(
//                        "IntrBkSttlmAmt",
//                        "MXFMT04",
//                        "Amount must be > 0"
//                ));
//            }
//        } catch (Exception e) {
//            errors.add(new ValidationError(
//                    "IntrBkSttlmAmt",
//                    "MXFMT05",
//                    "Invalid decimal amount"
//            ));
//        }
//    }
//
//    private void checkCurrency(SwiftAst ast, List<ValidationError> errors) {
//        FieldNode f = ast.getField("CdtTrfTxInf.IntrBkSttlmAmt.Ccy");
//        if (f == null) return;
//
//        String ccy = f.getValue();
//        if (ccy.length() != 3 || !ccy.chars().allMatch(Character::isLetter)) {
//            errors.add(new ValidationError(
//                    "Ccy",
//                    "MXFMT06",
//                    "Invalid currency code"
//            ));
//        }
//    }
//
//    // =====================================================
//    // BUSINESS RULES
//    // =====================================================
//
//    private void checkNbOfTxsConsistency(
//            SwiftAst ast,
//            List<ValidationError> errors
//    ) {
//        FieldNode n = ast.getField("GrpHdr.NbOfTxs");
//        if (n == null) return;
//
//        int declared = Integer.parseInt(n.getValue());
//
//        int actual =
//                ast.getFieldsByPrefix("CdtTrfTxInf").size();
//
//        if (declared != actual) {
//            errors.add(new ValidationError(
//                    "GrpHdr.NbOfTxs",
//                    "MXBUS01",
//                    "NbOfTxs does not match transaction count"
//            ));
//        }
//    }
//}