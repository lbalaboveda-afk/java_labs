document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('calcForm');

    if (form) {
        initCalculatorForm(form);
        return;
    }

    initResultPage();
});

function initCalculatorForm(form) {
    form.addEventListener('submit', (event) => {
        const bValue = document.getElementById('b').value;
        const op = form.querySelector('input[name="op"]:checked').value;
        const b = parseFloat(bValue);

        if (op === 'div' && (isNaN(b) || b === 0)) {
            event.preventDefault();
            alert('Деление на ноль или некорректное число невозможно.');
        }
    });
}

function initResultPage() {
    const params = new URLSearchParams(window.location.search);
    const a = parseFloat(params.get('a'));
    const b = parseFloat(params.get('b'));
    const op = params.get('op');
    const valueCell = document.getElementById('resultValue');

    if (isNaN(a) || isNaN(b) || !op) {
        valueCell.textContent = 'Некорректные данные. Вернитесь и введите числа.';
        return;
    }

    const operations = {
        add: { label: '+', calc: (x, y) => x + y },
        sub: { label: '−', calc: (x, y) => x - y },
        mul: { label: '×', calc: (x, y) => x * y },
        div: { label: '÷', calc: (x, y) => y === 0 ? NaN : x / y }
    };

    const opInfo = operations[op];

    if (!opInfo) {
        valueCell.textContent = 'Неизвестная операция';
        return;
    }

    let result = opInfo.calc(a, b);

    if (!isNaN(result)) {
        result = Number(result.toFixed(4));
    }

    document.getElementById('argA').textContent = a;
    document.getElementById('argB').textContent = b;
    document.getElementById('opLabel').textContent = opInfo.label;
    valueCell.textContent = isNaN(result) ? 'Ошибка' : result;
}