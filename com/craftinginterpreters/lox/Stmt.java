package com.craftinginterpreters.lox;

import java.util.List;

sealed interface Stmt permits Stmt.Expression, Stmt.Print, Stmt.Var, Stmt.Block {

  static final class Expression implements Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;
  }

  static final class Print implements Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    final Expr expression;
  }

  static final class Var implements Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    final Token name;
    final Expr initializer;
  }

  static final class Block implements Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    final List<Stmt> statements;
  }
}
