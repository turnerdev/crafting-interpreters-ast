package com.craftinginterpreters.lox;

import java.util.List;

class Interpreter {
  private Environment environment = new Environment();

  void interpret(Expr expression) {
    try {
      Object value = evaluate(expression);
      System.out.println(stringify(value));
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        evaluate(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  Object evaluate(Stmt stmt) {
    return switch (stmt) {
      case Stmt.Expression s -> evaluate(s.expression);
      case Stmt.Print s -> {
        Object value = evaluate(s.expression);
        System.out.println(stringify(value));
        yield null;
      }
      case Stmt.Var s -> {
        Object value = null;
        if (s.initializer != null) {
          value = evaluate(s.initializer);
        }
        environment.define(s.name.lexeme, value);
        yield null;
      }
      case Stmt.Block s -> {
        executeBlock(s.statements, new Environment(environment));
        yield null;
      }
    };
  }

  Object evaluate(Expr expr) {
    return switch (expr) {
      case Expr.Grouping e -> evaluate(e.expression);
      case Expr.Literal e -> e.value;
      case Expr.Variable e -> environment.get(e.name);
      case Expr.Assign e -> {
        Object value = evaluate(e.value);
        environment.assign(e.name, value);
        yield value;
      }
      case Expr.Unary e -> {
        Object right = evaluate(e.right);
        switch (e.operator.type) {
          case MINUS:
            checkNumberOperand(e.operator, right);
            yield -(double) right;
          case BANG:
            yield !isTruthy(right);
        }
        yield null;
      }
      case Expr.Binary e -> {
        Object left = evaluate(e.left);
        Object right = evaluate(e.right);
        switch (e.operator.type) {
          case MINUS:
            checkNumberOperands(e.operator, left, right);
            yield (double) left - (double) right;
          case SLASH:
            checkNumberOperands(e.operator, left, right);
            yield (double) left / (double) right;
          case STAR:
            checkNumberOperands(e.operator, left, right);
            yield (double) left * (double) right;
          case PLUS:
            if (left instanceof Double && right instanceof Double) {
              yield (double) left + (double) right;
            }
            if (left instanceof String && right instanceof String) {
              yield (String) left + (String) right;
            }
            throw new RuntimeError(e.operator, "Operands must be two numbers or two strings.");
          case GREATER:
            checkNumberOperands(e.operator, left, right);
            yield (double) left > (double) right;
          case GREATER_EQUAL:
            checkNumberOperands(e.operator, left, right);
            yield (double) left >= (double) right;
          case LESS:
            checkNumberOperands(e.operator, left, right);
            yield (double) left < (double) right;
          case LESS_EQUAL:
            checkNumberOperands(e.operator, left, right);
            yield (double) left <= (double) right;
          case BANG_EQUAL:
            yield !isEqual(left, right);
          case EQUAL_EQUAL:
            yield isEqual(left, right);
        }
        yield null;
      }
    };
  }

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }

  void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;

      for (Stmt statement : statements) {
        evaluate(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;

    throw new RuntimeError(operator, "Operands must be numbers.");
  }
}
