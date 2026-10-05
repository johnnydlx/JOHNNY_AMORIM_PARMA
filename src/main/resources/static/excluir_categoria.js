const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

document.querySelectorAll('.excluir-categoria').forEach(function (button) {
    button.addEventListener('click', async function () {
        const id = this.dataset.id;
        const nome = this.dataset.nome || 'esta categoria';

        if (!confirm('Deseja realmente excluir "' + nome + '"?')) {
            return;
        }

        try {
            const response = await fetch('/categoriaexcluir/' + id, {
                method: 'DELETE',
                headers: csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {}
            });

            if (!response.ok) {
                alert('Nao foi possivel excluir a categoria.');
                return;
            }

            window.location.href = '/categorias';
        } catch (error) {
            alert('Erro ao comunicar com o servidor.');
        }
    });
});
