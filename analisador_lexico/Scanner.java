package analisador_lexico;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private byte[] input;
    private int current;
    private int start;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("class", TokenType.KEYWORD);
        keywords.put("constructor", TokenType.KEYWORD);
        keywords.put("function", TokenType.KEYWORD);
        keywords.put("method", TokenType.KEYWORD);
        keywords.put("field", TokenType.KEYWORD);
        keywords.put("static", TokenType.KEYWORD);
        keywords.put("var", TokenType.KEYWORD);
        keywords.put("int", TokenType.KEYWORD);
        keywords.put("char", TokenType.KEYWORD);
        keywords.put("boolean", TokenType.KEYWORD);
        keywords.put("void", TokenType.KEYWORD);
        keywords.put("true", TokenType.KEYWORD);
        keywords.put("false", TokenType.KEYWORD);
        keywords.put("null", TokenType.KEYWORD);
        keywords.put("this", TokenType.KEYWORD);
        keywords.put("let", TokenType.KEYWORD);
        keywords.put("do", TokenType.KEYWORD);
        keywords.put("if", TokenType.KEYWORD);
        keywords.put("else", TokenType.KEYWORD);
        keywords.put("while", TokenType.KEYWORD);
        keywords.put("return", TokenType.KEYWORD);
    }

    public Scanner(byte[] input) {
        this.input = input;
        current = 0;
        start = 0;
    }

    private char peek() {
        if (current < input.length)
            return (char) input[current];
        return '\0';
    }

    private char peekNext () {
        int next = current + 1;
        if ( next  < input.length) {
            return (char)input[next];
        } else {
            return 0;
        }
   }

    private void advance() {
        char ch = peek();
        if (ch != '\0') {
            current++;
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || Character.isDigit((c));
    }

    public Token nextToken() {
        skipWhitespace();
        char ch = peek();

        if (isAlpha(ch)) {
            return identifier();
        }

        if (ch == '0') {
            advance();
            return new Token(TokenType.NUMBER, Character.toString(ch));
        } else if (Character.isDigit(ch))
            return number();

        else if (Character.isDigit(ch))
            return number();

        switch (ch) {
            case '+':
                advance();
                return new Token(TokenType.SYMBOL, "+");
            case '-':
                advance();
                return new Token(TokenType.SYMBOL, "-");
            case '*':
                advance();
                return new Token(TokenType.SYMBOL, "*");
            case '{':
                advance();
                return new Token(TokenType.SYMBOL, "{");
            case '}':
                advance();
                return new Token(TokenType.SYMBOL, "}");
            case '(':
                advance();
                return new Token(TokenType.SYMBOL, "(");
            case ')':
                advance();
                return new Token(TokenType.SYMBOL, ")");
            case '[':
                advance();
                return new Token(TokenType.SYMBOL, "[");
            case ']':
                advance();
                return new Token(TokenType.SYMBOL, "]");
            case '.':
                advance();
                return new Token(TokenType.SYMBOL, ".");
            case ',':
                advance();
                return new Token(TokenType.SYMBOL, ",");
            case ';':
                advance();
                return new Token(TokenType.SYMBOL, ";");
            case '&':
                advance();
                return new Token(TokenType.SYMBOL, "&");
            case '|':
                advance();
                return new Token(TokenType.SYMBOL, "|");
            case '<':
                advance();
                return new Token(TokenType.SYMBOL, "<");
            case '>':
                advance();
                return new Token(TokenType.SYMBOL, ">");
            case '~':
                advance();
                return new Token(TokenType.SYMBOL, "*");
            case '=':
                advance();
                return new Token(TokenType.SYMBOL, "=");
            case 0:
                return new Token(TokenType.EOF, "EOF");
            case '"':
            return string();
            case '/':
                if (peekNext() == '/') {
                    skipLineComments();
                    return nextToken();
                } else if (peekNext() == '*') {
                    skipBlockComments();
                    return nextToken();
                }
                else {
                    advance();
                    return new Token (TokenType.SYMBOL,"/");
                }
            default:
                advance(); 
                return new Token(TokenType.ILLEGAL, Character.toString(ch));
        }
    }

    private Token string () {
        advance();
        start = current;
        while (peek() != '"' && peek() != 0) {
            advance();
        }
        String s = new String(input, start, current-start, StandardCharsets.UTF_8);
        Token token = new Token (TokenType.STRINGCONST,s);
        advance();
        return token;
    }


    private Token number() {
        int start = current;
        while (Character.isDigit(peek())) {
            advance();
        }

        String n = new String(input, start, current - start);
        return new Token(TokenType.NUMBER, n);
    }

    private Token identifier() {
        int start = current;
        while (isAlphaNumeric(peek())) advance();
    
        String id = new String(input, start, current-start)  ;
        TokenType type = keywords.get(id);
        if (type == null) type = TokenType.IDENT;
        return new Token(type, id);
    }

    private void skipWhitespace() {
        char ch = peek();
        while (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
            advance();
            ch = peek();
        }
    }

    private void skipLineComments() {
        for (char ch = peek(); ch != '\n' && ch != 0;  advance(), ch = peek()) ;
    }

    private void skipBlockComments() {
        boolean endComment = false;
        advance();

        while (!endComment) {
            advance();
            char ch = peek();
            if ( ch == 0) { // eof
                System.exit(1);
            }

            if (ch == '*') {

               for (ch = peek(); ch == '*';  advance(), ch = peek()) ;
                if (ch == '/') {
                    endComment = true;
                    advance();
                }
            }
        }
    }
}
