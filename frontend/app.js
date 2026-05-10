let currentUser = null;
async function sendPost() {
    const formData = new FormData();
    
    const postData = {
        content: "試行錯誤のメモです",
        tags: ["Java", "JS"],
        visibility: "public"
    };
    

    formData.append("data", new Blob([JSON.stringify(postData)], {
        type: "application/json"
    }));
    
  
    const fileInput = document.getElementById('fileInput');
    if (fileInput.files[0]) {
        formData.append("file", fileInput.files[0]);
    }

    const response = await fetch('/api/v1/posts', {
        method: 'POST',
        body: formData
    });

    const result = await response.text();
    alert(result);
}

function explanation(){
    const openBtn = document.getElementById('openModal');
    const closeBtn = document.getElementById('closeModal');
    const modal = document.getElementById('modalOverlay');

    if(!openBtn || !closeBtn || !modal) return;//for crash prevention
    
    openBtn.addEventListener('click', () => {
        modal.classList.add('active');
    });

    closeBtn.addEventListener('click', () => {
        modal.classList.remove('active');
    });

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
}
function form(){
        const formElement = document.getElementById('postForm');
        if (!formElement) return;
        formElement.addEventListener('submit', async (e) => {e.preventDefault();
        const formData = new FormData();
            
        const data = {
            content: document.getElementById('content').value,
            tags: document.getElementById('tags').value.split(','),
            visibility: document.getElementById('visibility').value
        };
        const jsonBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
        formData.append('data', jsonBlob);
           
        const fileInput = document.getElementById('file');
        if (fileInput.files[0]) {
            formData.append('file', fileInput.files[0]);
        }

        const response = await fetch('/api/v1/posts', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('succeeded post!');
        } else {
            alert('happen error...');
        }
    });
}
function createPostCard(post) {
    console.log("投稿ID"+post.id);
    const currentUserId = currentUser ? currentUser.id : null;
    console.log("ログイン中の人"+currentUserId);
    const postUserId = post.user ? post.user.id : null;
    console.log("投稿者ID"+postUserId);
    console.log("一致判定"+(currentUserId === postUserId));

    const tagsHtml = post.tags.map(tag => `<span class="tag">#${tag.tagName}</span>`).join(' ');
    
    const imageHtml = post.imageUrl ? `<img src="${post.imageUrl}" class="post-image" alt="投稿画像">` : '';

    let deleteBtnHtml = '';
    if(currentUser){
        if(currentUser.role == 'ADMIN'){
            deleteBtnHtml = `<button onclick="deletePost(${post.id})" class="delete-btn" style="background:red; color:white; border:none; padding:5px; margin-right:5px; cursor:pointer;">削除</button>`;
        }
        else if(post.user && currentUser.id === post.user.id){
            deleteBtnHtml = `<button onclick="deletePost(${post.id})" class="delete-btn" style="background:red; color:white; border:none; padding:5px; margin-right:5px; cursor:pointer;">削除</button>`;
        }
    }
    return `
        <div class="post-card">
            <div class="post-header">
                <span class="post-id">ID: ${post.id}</span>
                <span class="post-date">${post.createdAt || ''}</span>
            </div>
            <div class="post-content">
                <p>${post.content}</p>
                ${imageHtml}
            </div>
            <div class="post-tags">
                ${tagsHtml}
            </div>
            <div class="post-actions">
                ${deleteBtnHtml} <a href="search.html?related=${post.id}" class="related-btn">関連投稿を見る</a>
            </div>
        </div>
    `;
}
async function deletePost(postId) {
    if (!confirm('この投稿を削除しますか？')) return;

    try{
        const res = await fetch(`/api/v1/posts/${postId}`, {method: 'DELETE'});
        if(res.ok){
            alert('削除しました');
            window.location.reload();
        } else {
            const msg = await res.text();
            alert('削除に失敗しました: ' + msg);
        }
    } catch (e) {
        alert('削除中にエラーが発生しました');
    }
}

async function initSearchPage() {
    const resultsContainer = document.getElementById('resultsContainer');
    if (!resultsContainer) return; 

    const params = new URLSearchParams(window.location.search);
    const keyword = params.get('keyword');
    const tag = params.get('tag');
    const relatedId = params.get('related'); 

    let url = '/api/v1/posts/search'; 

    if (keyword) {
        url += `?keyword=${encodeURIComponent(keyword)}`;
        document.querySelector('h2').textContent = `"${keyword}" の検索結果`;
    } else if (tag) {
        url += `?tag=${encodeURIComponent(tag)}`;
        document.querySelector('h2').textContent = `#${tag} の検索結果`;
    } else if (relatedId) {
        url = `/api/v1/posts/related?id=${relatedId}`; 
        document.querySelector('h2').textContent = `ID:${relatedId} の関連投稿`;
    } else {
        url = '/api/v1/posts/search'; 
        document.querySelector('h2').textContent = `全件表示`;
    }

    try {
        const response = await fetch(url);
        const posts = await response.json();

        if (posts.length === 0) {
            resultsContainer.innerHTML = '<p>投稿が見つかりませんでした。</p>';
        } else {
            resultsContainer.innerHTML = posts.map(post => createPostCard(post)).join('');
        }
    } catch (error) {
        console.error('Error:', error);
        resultsContainer.innerHTML = '<p>エラーが発生しました。</p>';
    }
}
function setupSearch() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');

    if (searchBtn && searchInput) {
        searchBtn.addEventListener('click', () => {
            const val = searchInput.value;
            window.location.href = `search.html?keyword=${encodeURIComponent(val)}`;
        });
    }
}
function setupImagePreview() {
    const fileInput = document.getElementById('file');
    const previewArea = document.getElementById('previewArea');
    const imagePreview = document.getElementById('imagePreview');

    if(!fileInput || !previewArea || !imagePreview) return;

    fileInput.addEventListener('change', () => {
        const file = fileInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                previewArea.style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            imagePreview.src = '';
            previewArea.style.display = 'none';
        }
    });
}
function setupAuth(){
    const modal = document.getElementById('loginModalOverlay');
    const openBtn = document.getElementById('openLoginModal');
    const closeBtn = document.getElementById('closeLoginModal');

    const loginArea = document.getElementById('loginFormArea');
    const registerArea = document.getElementById('registerFormArea');
    
    const showLoginBtn = document.getElementById('showLoginBtn');
    const showRegisterBtn = document.getElementById('showRegisterBtn');
    const doLoginBtn = document.getElementById('doLoginBtn');
    const doRegisterBtn = document.getElementById('doRegisterBtn');

    const msg = document.getElementById('authMessage');
    if(!modal||!openBtn||!closeBtn||!loginArea||!registerArea||!showLoginBtn||!showRegisterBtn||!doLoginBtn||!doRegisterBtn) return;

    if(openBtn){
        openBtn.addEventListener('click', () => {modal.classList.add('active');});
        closeBtn.addEventListener('click', () => {modal.classList.remove('active');});
        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    } 
    showLoginBtn.addEventListener('click', () => {
        loginArea.style.display = 'block';
        registerArea.style.display = 'none';
        showLoginBtn.style.background = "#0277bd";
        showLoginBtn.style.color = "#ccc";
        msg.textContent = '';
    }); 
    showRegisterBtn.addEventListener('click', () => {
        loginArea.style.display = 'none';
        registerArea.style.display = 'block';
        showLoginBtn.style.background = "#ccc";
        showRegisterBtn.style.background = "#0277bd";
        showRegisterBtn.style.color = "#fff";
        msg.textContent = '';
    });

    doLoginBtn.addEventListener('click', async () => {
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;
        const res = await fetch('/api/v1/users/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });
        if(res.ok) {
            msg.textContent = 'ログイン成功';
            modal.classList.remove('active');
            clickLoginStatus();
        } else {
            msg.textContent = 'ログイン失敗';
        }
    });

    doRegisterBtn.addEventListener('click', async () => {
        const username = document.getElementById('regUsername').value;
        const password = document.getElementById('regPassword').value;
        const res = await fetch('/api/v1/users/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });
        if(res.ok) {
            alert('登録成功！ログインしてください。');
            showLoginBtn.click();
        } else {
            const errorText = await res.text();
            msg.textContent = '登録失敗'+errorText;
        }
    });
}
async function clickLoginStatus() {
    try{
        const res = await fetch('/api/v1/users/me');
        
        if(res.ok){
            const user = await res.json();
            currentUser = user;
            console.log("login ok", currentUser.username);
        }
        else{
            currentUser = null;
            console.log("guest user");
        }
    } catch(err){
        currentUser = null;
        console.log("not logged in");
    }
    const openBtn = document.getElementById('openLoginModal');
    if(openBtn && currentUser){
        openBtn.textContent = currentUser.username+" (ログアウト)";
        openBtn.style.background = "#4caf50";

        openBtn.onclick = async (e) => {
        e.stopPropagation();
            if(confirm("ログアウトしますか？")){
                await fetch('/api/v1/users/logout', {method: 'POST'});
                window.location.reload();
            }
        };
    }
} 


document.addEventListener('DOMContentLoaded', async () => {
    explanation();
    form(); 
    setupSearch();
    setupImagePreview();
    setupAuth();
    await clickLoginStatus();
    initSearchPage();
});