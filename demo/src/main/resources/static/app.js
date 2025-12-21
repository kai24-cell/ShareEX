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