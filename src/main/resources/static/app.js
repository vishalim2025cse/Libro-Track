const api = '/api';
let searchTimer;

const $ = id => document.getElementById(id);

function showSection(id){
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-btn').forEach(b => b.classList.toggle('active', b.dataset.section === id));
  $(id).classList.add('active');
  $('page-title').textContent = id === 'dashboard' ? 'Dashboard' : id === 'books' ? 'Books' : id === 'students' ? 'Students' : 'Issues & Returns';
  if(id==='dashboard') loadDashboard();
  if(id==='books') loadBooks();
  if(id==='students') loadStudents();
  if(id==='issues') loadIssues();
}

document.querySelectorAll('.nav-btn').forEach(btn => btn.addEventListener('click', () => showSection(btn.dataset.section)));

function openModal(id){ $(id).classList.add('show'); }
function closeModal(id){ $(id).classList.remove('show'); }
function toast(message, error=false){
  const t=$('toast'); t.textContent=message; t.style.borderColor=error?'#7f1d1d':'#38383f'; t.classList.add('show');
  setTimeout(()=>t.classList.remove('show'),2800);
}

async function request(url, options={}){
  const res = await fetch(api+url, {headers:{'Content-Type':'application/json', ...(options.headers||{})}, ...options});
  const text = await res.text();
  let data; try { data = text ? JSON.parse(text) : null; } catch { data = {message:text}; }
  if(!res.ok) throw new Error(data?.message || data?.error || 'Request failed');
  return data;
}

async function loadDashboard(){
  try{
    const d=await request('/dashboard');
    $('totalBooks').textContent=d.totalBooks;
    $('totalStudents').textContent=d.totalStudents;
    $('totalCopies').textContent=d.totalCopies;
    $('availableCopies').textContent=d.availableCopies;
    $('currentlyIssued').textContent=d.currentlyIssued;
    $('totalFine').textContent='₹'+Number(d.totalFineCollected).toFixed(2);
  }catch(e){toast(e.message,true)}
}

async function loadBooks(){
  try{
    const q=$('bookSearch').value.trim();
    const books=await request('/books'+(q?'?keyword='+encodeURIComponent(q):''));
    $('booksTable').innerHTML=books.length?books.map(b=>`<tr>
      <td><b>${esc(b.title)}</b></td><td>${esc(b.author)}</td><td>${esc(b.isbn)}</td><td>${esc(b.category)}</td>
      <td>${b.totalCopies}</td><td>${b.availableCopies}</td>
      <td><button class="delete-btn" onclick="deleteBook(${b.id})">Delete</button></td></tr>`).join(''):
      '<tr><td colspan="7">No books found.</td></tr>';
  }catch(e){toast(e.message,true)}
}
function debouncedBooks(){clearTimeout(searchTimer);searchTimer=setTimeout(loadBooks,250)}

async function loadStudents(){
  try{
    const students=await request('/students');
    $('studentsTable').innerHTML=students.length?students.map(s=>`<tr><td>${s.id}</td><td><b>${esc(s.name)}</b></td><td>${esc(s.email)}</td><td><button class="delete-btn" onclick="deleteStudent(${s.id})">Delete</button></td></tr>`).join(''):'<tr><td colspan="4">No students found.</td></tr>';
  }catch(e){toast(e.message,true)}
}

async function loadIssues(){
  try{
    const sid=$('studentFilter').value.trim();
    const active=$('activeOnly').checked;
    const url=sid?`/issues/student/${sid}?activeOnly=${active}`:`/issues`;
    const issues=await request(url);
    $('issuesTable').innerHTML=issues.length?issues.map(i=>`<tr>
      <td>${i.id}</td><td><b>${esc(i.bookTitle)}</b> (#${i.bookId})</td><td>${esc(i.studentName)} (#${i.studentId})</td>
      <td>${i.issueDate}</td><td>${i.dueDate}</td><td>${i.returnDate||'-'}</td><td>₹${Number(i.fineAmount).toFixed(2)}</td>
      <td><span class="badge ${i.status==='ISSUED'?'issued':'returned'}">${i.status}</span></td>
      <td>${i.status==='ISSUED'?`<button class="return-btn" onclick="returnBook(${i.id})">Return</button>`:'-'}</td></tr>`).join(''):
      '<tr><td colspan="9">No issue records found.</td></tr>';
  }catch(e){toast(e.message,true)}
}

$('bookForm').addEventListener('submit',async e=>{
  e.preventDefault(); const f=new FormData(e.target); const body=Object.fromEntries(f.entries()); body.totalCopies=Number(body.totalCopies);
  try{await request('/books',{method:'POST',body:JSON.stringify(body)});closeModal('bookModal');e.target.reset();toast('Book added successfully');loadBooks();loadDashboard();}catch(x){toast(x.message,true)}
});

$('studentForm').addEventListener('submit',async e=>{
  e.preventDefault(); const body=Object.fromEntries(new FormData(e.target).entries());
  try{await request('/students',{method:'POST',body:JSON.stringify(body)});closeModal('studentModal');e.target.reset();toast('Student added successfully');loadStudents();loadDashboard();}catch(x){toast(x.message,true)}
});

$('issueForm').addEventListener('submit',async e=>{
  e.preventDefault(); const body=Object.fromEntries(new FormData(e.target).entries()); body.bookId=Number(body.bookId);body.studentId=Number(body.studentId);
  try{await request('/issues',{method:'POST',body:JSON.stringify(body)});closeModal('issueModal');e.target.reset();toast('Book issued. Due date is 14 days from today.');loadIssues();loadBooks();loadDashboard();}catch(x){toast(x.message,true)}
});

async function returnBook(id){
  if(!confirm('Return this book now?')) return;
  try{const r=await request(`/issues/${id}/return`,{method:'PUT'});toast(`Book returned. Fine: ₹${Number(r.fineAmount).toFixed(2)}`);loadIssues();loadBooks();loadDashboard();}catch(e){toast(e.message,true)}
}
async function deleteBook(id){
  if(!confirm('Delete this book?')) return;
  try{await request(`/books/${id}`,{method:'DELETE'});toast('Book deleted');loadBooks();loadDashboard();}catch(e){toast(e.message,true)}
}
async function deleteStudent(id){
  if(!confirm('Delete this student?')) return;
  try{await request(`/students/${id}`,{method:'DELETE'});toast('Student deleted');loadStudents();loadDashboard();}catch(e){toast(e.message,true)}
}
function esc(v){return String(v??'').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));}

loadDashboard();
