package com.craftinginterpreters.lox;

public class AstPrinter implements Expr.Visitor<String> {

    static enum ParType {
        normal,
        rpn
    };

    static ParType currentParType = ParType.normal;

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));

        // (1 + 2) * (4 - 3)
        Expr expr2 = new Expr.Binary(
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(1),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal(2))),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(4),
                                new Token(TokenType.MINUS, "-", null, 1),
                                new Expr.Literal(3))));

        currentParType = ParType.normal;
        System.out.println(new AstPrinter().print(expression));

        currentParType = ParType.rpn;
        System.out.println(new AstPrinter().print(expr2));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitTernaryExpr(Expr.Ternary expr) {
        return parenthesize("ternary", expr.condition, expr.thenBranch, expr.elseBranch);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        if (currentParType == ParType.normal) {
            builder.append("(").append(name);
            for (Expr expr : exprs) {
                builder.append(" ").append(expr.accept(this));
            }
            builder.append(")");
        } else if (currentParType == ParType.rpn) {
            builder.append("(");
            for (Expr expr : exprs) {
                builder.append(expr.accept(this));
                builder.append(" ");
            }
            builder.append(name).append(")");
        }

        return builder.toString();
    }

}