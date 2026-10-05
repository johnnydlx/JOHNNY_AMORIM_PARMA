const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

// Adicione um ouvinte de eventos aos botoes de exclusao
document.querySelectorAll('.excluir').forEach(function(button) {
    button.addEventListener('click',
    function() {
        if (confirm('Confirma a exclusao?')) {

            const linha = this.closest('tr'); // Obtem a linha atual da tabela

            const id = this.dataset.id;

            //console.log("id=" + id);

            // Realize a chamada AJAX para excluir o recurso
            fetch(`/usuarioexcluir/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    ...(csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {})
                },
            })
            .then(response => {
                if (response.ok) {
                    // A exclusao foi bem-sucedida
                    console.log('Usuario excluido com sucesso.');

                    // Remove a linha da tabela apos a exclusao
                    linha.remove();
                } else {
                    // A solicitacao DELETE falhou
                    console.error('Erro ao excluir usuario.');
                    alert('Erro ao excluir usuario');
                }
            })
            .catch(error => {
                // Lidar com erros de rede ou outros erros
                console.error('Erro de rede:', error);
                alert('Erro de rede:' + error);
            });
        }
    });
});
