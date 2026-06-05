package com.MT_MX.demo.semantic.parser;
public class SwiftBlockParser {

    public SwiftBlocks parse(String raw) throws Exception {
        SwiftBlocks blocks = new SwiftBlocks();
        int i = 0;
        while (i < raw.length()) {

            if (raw.charAt(i) == '{') {

                int end = findMatchingBrace(raw, i);

                int colonIndex = raw.indexOf(':', i);
                if (colonIndex == -1 || colonIndex > end) {
                    throw new Exception(
                            "Invalid block format at position " + i
                    );
                }

                int blockNo;
                try {
                    blockNo = Integer.parseInt(raw.substring(i + 1, colonIndex));
                } catch (NumberFormatException e) {
                    throw new Exception(
                            "Invalid block number at position " + i
                    );
                }

                String content = raw.substring(colonIndex + 1, end);

                switch (blockNo) {
                    case 1 -> blocks.setBlock1(content);
                    case 2 -> blocks.setBlock2(content);
                    case 3 -> blocks.setBlock3(content);
                    case 4 -> {
                        if (content.endsWith("-")) {
                            content = content.substring(0, content.length() - 1);
                        }
                        blocks.setBlock4(content);
                    }
                    case 5 -> blocks.setBlock5(content);
                    default -> {

                    }
                }

                i = end + 1;

            } else {
                i++;
            }
        }
//        while (i < raw.length()) {
//            if (raw.charAt(i) == '{') {
//                int blockNo = raw.charAt(i + 1) - '0';
//                System.out.println(blockNo);
//                int end = findMatchingBrace(raw, i);
//                String content = raw.substring(i + 3, end);
//
//                switch (blockNo) {
//                    case 1 -> blocks.setBlock1(content);
//                    case 2 -> blocks.setBlock2(content);
//                    case 3 ->blocks.setBlock3(content);
//                    case 4 -> blocks.setBlock4(content);
//                    case 5 -> blocks.setBlock5(content);
//                }
//                i = end + 1;
//            } else {
//                i++;
//            }
//        }
//
        System.out.println(blocks);
        return blocks;
    }

    private int findMatchingBrace(String s, int start) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '{') depth++;
            if (s.charAt(i) == '}') depth--;
            if (depth == 0) return i;
        }
        throw new RuntimeException("Unbalanced braces in SWIFT message");
    }
}
