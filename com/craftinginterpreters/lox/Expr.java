package com.craftinginterpreters.lox;

sealed interface Expr permits Expr.Binary, Expr.Grouping, Expr.Literal, Expr.Unary {

  static final class Binary implements Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static final class Grouping implements Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;
  }

  static final class Literal implements Expr {
    Literal(Object value) {
      this.value = value;
    }

    final Object value;
  }

  static final class Unary implements Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;
  }
}
