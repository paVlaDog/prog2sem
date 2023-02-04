function Expression(specFunc, diffVar, sign, ...args) {
    // :NOTE: Прототипы
    this.specFunc = specFunc;
    this.diffVar = diffVar;
    this.sign = sign;
    this.args = args;
}
Expression.prototype.evaluate = function (...xyz) {
    return this.specFunc(...this.args.map(expr => expr.evaluate(...xyz)));
};
Expression.prototype.toString = function () {
    return (this.args.join(" ") + " " + this.sign).trim();
};
Expression.prototype.prefix = function () {
    return this.args.length === 0 ? this.sign.toString() : "(" + this.sign + " " + this.args.map(expr => expr.prefix()).join(" ") + ")";
};
Expression.prototype.postfix = function () {
    return this.args.length === 0 ? this.sign.toString() : "(" + this.args.map(expr => expr.postfix()).join(" ") + " " + this.sign + ")";
};
Expression.prototype.diff = function (arg) {
    return this.diffVar(...this.args)(arg, ...this.args.map(expr => expr.diff(arg)));
};

function create(specFunc, diffVar, sign) {
    function CreateConstructor(...args) {
        Expression.call(this, specFunc, diffVar, sign, ...args);
    }
    CreateConstructor.prototype = Object.create(Expression.prototype);
    CreateConstructor.constructor = CreateConstructor;
    return CreateConstructor;
}

function Const(arg) {
    this.evaluate = () => +arg;
    this.toString = () => arg.toString();
    this.prefix = () => arg.toString();
    this.postfix = () => arg.toString();
    this.diff = (arg) => const0;
}
const const0 = new Const(0);
const const1 = new Const(1);
const const2 = new Const(2);
const CONST_E = new Const(Math.E);

variables = ["x", "y", "z"];
function Variable(str) {
    this.evaluate = (...args) => args[variables.indexOf(str)];
    this.toString = () => str;
    this.prefix = () => str;
    this.postfix = () => str;
    this.diff = (arg) => str === arg ? const1 : const0;
}

const Add = create((a, b) => a + b,
    (a, b) => (arg, aDiff, bDiff) => new Add(aDiff, bDiff),
    "+");
const Subtract = create((a, b) => a - b,
    (a, b) => (arg, aDiff, bDiff) => new Subtract(aDiff, bDiff),
    "-");
const Negate = create((a) => -a,
    (num) => (arg, numDiff) => new Negate(numDiff),
    "negate");
const Multiply = create((a, b) => a * b,
    (a, b) => (arg, aDiff, bDiff) => new Add(new Multiply(aDiff, b), new Multiply(a, bDiff)),
    "*");
const Divide = create((a, b) => a / b,
    (a, b) => (arg, aDiff, bDiff) => new Divide(
        new Subtract(new Multiply(aDiff, b), new Multiply(a, bDiff)),
        new Multiply(b, b)),
    "/");
const Pow = create(Math.pow,
    (a, b) => (arg, aDiff, bDiff) => new Multiply(
        new Pow(a, new Subtract(b, const1)),
        new Add(new Multiply(b, aDiff), new Multiply(new Multiply(a, new Log(CONST_E, a)), bDiff))),
    "pow");
const Log = create((a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    (a, b) => (arg, aDiff, bDiff) => new Divide(new Subtract(new Divide(new Multiply(
        new Log(CONST_E, a), bDiff), b), new Divide(new Multiply(
        new Log(CONST_E, b), aDiff), a)), new Pow(new Log(CONST_E, a), const2)),
    "log");
const Mean = create((...meanArgs) => meanArgs.reduce((a, b) => (a + b), 0) / meanArgs.length,
    (...meanArgs) => (arg, ...meanArgsDiff) => new Mean(...meanArgsDiff),
    "mean");
const Var = create((...args) => args.reduce((a, b) => (a + Math.pow(b, 2)), 0) /
        args.length - Math.pow((args.reduce((a, b) => (a + b)) / args.length), 2),
    (...meanArgs) => (arg, ...meanArgsDiff) => new Mean(...meanArgs.map(expr => new Multiply(const2, new Multiply(
        new Subtract(new Mean(...meanArgs), expr),
        new Subtract(new Mean(...meanArgsDiff), expr.diff(arg)))))),
    "var");

operations = new Map([
    ["+", Add],
    ["-", Subtract],
    ["*", Multiply],
    ["/", Divide],
    ["negate", Negate],
    ["log", Log],
    ["pow", Pow],
    ["mean", Mean],
    ["var", Var]
]);

function getArity(expression) {
    return new expression(1, 2).specFunc.length;
}

function parse(str) {
    const stack = [];
    const expr = str.trim().split(/\s+/);
    const pushing = (f) => (x) => stack.push(new f(...stack.splice(-x)));
    for (const v of expr) {
        if (operations.has(v)) {
            pushing(operations.get(v))(getArity(operations.get(v)));
        } else if (isFinite(v)) {
            stack.push(new Const(parseInt(v)));
        } else if (variables.includes(v)) {
            stack.push(new Variable(v))
        }
    }
    return stack.pop();
}

function NewError(name) {
    function NewErrorConstructor(message) {
        Error.call(this, message);
        this.message = message;
    }
    NewErrorConstructor.prototype = Object.create(Error.prototype);
    NewErrorConstructor.prototype.name = name;
    return NewErrorConstructor;
}
const UnknownTokenError = NewError("UnknownTokenError");
const UnexpectedEndOfStrokeError = NewError("UnexpectedEndOfStrokeError");
const IllegalArity = NewError("IllegalArity");
const TokenAfterEndOfStroke = NewError("TokenAfterEndOfStroke");

const parsePrefix = stroke => parseWithBraces(stroke, "Prefix");
const parsePostfix = stroke => parseWithBraces(stroke, "Postfix");

function parseWithBraces(stroke, flag) {
    let index = 0;
    let str = stroke.replace(/[()]/g, x => ' ' + x + ' ').trim().split(/\s+/);
    let ans = parseOperands(flag);
    if (index < str.length) {
        throw new TokenAfterEndOfStroke("expected end of stroke, received:" + str[index]);
    }
    return ans;

    function parseOperands(flag) {
        if (index >= str.length) {
            throw new UnexpectedEndOfStrokeError("expected operation, received end of stroke");
        } else if (str[index] === "(" && ++index) {
            return parseOperation(flag);
        } else if (variables.includes(str[index])) {
            return new Variable(str[index++]);
        } else if (isFinite(str[index]) && str[index] !== "") {
            return new Const(parseInt(str[index++]));
        } else {
            throw new UnknownTokenError("expected operands, received:" + str[index]);
        }
    }

    function parseOperation(flag) {
        let stack = (flag === "Postfix") ? getArgument(flag) : undefined;
        if (operations.has(str[index++])) {
            return newOperation(operations.get(str[index - 1]), getArity(operations.get(str[index - 1])),
                (flag === "Prefix") ? getArgument(flag) : stack);
        } else {
            throw new UnknownTokenError("expected operation, received:" + str[index - 1]);
        }
    }

    function getArgument(flag){
        let stack = [];
        while (isFinite(str[index]) || variables.includes(str[index]) || str[index] === "("){
            stack.push(parseOperands(flag));
        }
        return stack;
    }

    function newOperation(obj, arity, stack) {
        if (index === str.length) {
            throw new UnexpectedEndOfStrokeError("expected operation, received end of stroke");
        } else if (str[index] !== ")") {
            throw new UnknownTokenError("expected ), received:" + str[index]);
        } else if (arity !== 0 && stack.length !== arity) {
            throw new IllegalArity("expected arity: " + arity + ", received:" + stack.length);
        } else {
            index++;
            return new obj(...stack);
        }
    }
}

