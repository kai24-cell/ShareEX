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
    document.getElementById('postForm').addEventListener('submit', async (e) => {
            e.preventDefault();

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

            const response = await fetch('/api/v1/posts', {//送信
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

document.addEventListener('DOMContentLoaded', () => {
    explanation();
    form(); 
});