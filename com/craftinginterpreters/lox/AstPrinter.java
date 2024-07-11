package com.craftinginterpreters.lox;

class AstPrinter {
  String print(Expr expr) {
    return switch (expr) {
      case Expr.Binary e -> parenthesize(e.operator.lexeme, e.left, e.right);
      case Expr.Grouping e -> parenthesize("group", e.expression);
      case Expr.Literal e -> e.value == null ? "nil" : e.value.toString();
      case Expr.Unary e -> parenthesize(e.operator.lexeme, e.right);
    };
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(this.print(expr));
    }
    builder.append(")");

    return builder.toString();
  }

  public static void main(String[] args) {
    Expr expression =
        new Expr.Binary(
            new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(new Expr.Literal(45.67)));

    System.out.println(new AstPrinter().print(expression));
  }
}
