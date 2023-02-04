const cnst = (number) => (x, y, z) => number;
const variable = (str) => str === 'x' ? (x, y, z) => x : (str === 'y' ? (x, y, z) => y : (x, y, z) => z);
const funcCreate = (specFunc) => (...args) => (x, y, z) => specFunc(...args.map(expr => expr(x, y, z)));

const negate = funcCreate((number) => -number);
const add = funcCreate((a, b) => a + b);
const subtract = funcCreate((a, b) => a - b);
const multiply = funcCreate((a, b) => a * b);
const divide = funcCreate((a, b) => a / b);
const abs = funcCreate(f => Math.abs(f));
const iff = funcCreate((bool, args1, args2) => bool >= 0 ? args1 : args2)

const pi = cnst(Math.PI)
const e = cnst(Math.E)

function parse (str){
    const stack = [];
    const expr = str.trim().split(/\s+/);
    const pushing = (f) => (x) => stack.push(f(...stack.splice(-x)));
    for (const v of expr) {
        if (v === "+") pushing(add)(2);
        else if (v === "-") pushing(subtract)(2);
        else if (v === "*") pushing(multiply)(2);
        else if (v === "/") pushing(divide)(2);
        else if (isFinite(v)) stack.push(cnst(parseInt(v)));
        else if (v === "negate") pushing(negate)(1);
        else if (v === "abs") pushing(abs)(1);
        else if (v === "iff") pushing(iff)(3);
        else if (v === "pi") stack.push(pi);
        else if (v === "e") stack.push(e);
        else stack.push(variable(v))
    }
    return stack.pop();
}
