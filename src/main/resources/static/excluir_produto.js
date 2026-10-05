const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

document.querySelectorAll('.excluir-produto').forEach(function (botao) {
    botao.addEventListener('click', async function () {
        const id = this.dataset.id;
        if (!confirm('Deseja realmente excluir este produto?')) return;

        try {
            const resposta = await fetch('/produtoexcluir/' + id, { method: 'DELETE', headers: csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {} });
            if (resposta.ok) {
                window.location.reload();
                return;
            }
            if (resposta.status === 403) {
                alert('Apenas administradores podem excluir produtos.');
                return;
            }
            if (resposta.status === 409) {
                alert('Este produto possui movimentações e não pode ser excluído. Inative o produto ou continue usando o histórico.');
                return;
            }
            alert('Nao foi possivel excluir o produto.');
        } catch (erro) {
            alert('Erro ao comunicar com o servidor.');
        }
    });
});
