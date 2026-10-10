function setText(id, value) { document.getElementById(id).textContent = value; }
function simpleAlert() { alert('Operation completed'); setText('alert-result', 'Simple alert accepted'); }
function confirmation() { setText('confirm-result', confirm('Are you sure?') ? 'Confirmed' : 'Cancelled'); }
function promptName() { const value = prompt('Enter your name:'); setText('prompt-result', value === null ? 'Prompt cancelled' : 'Hello, ' + value); }
