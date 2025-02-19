import kaggle
import os

def download_dataset():
    dataset = 'maca11/all-pokemon-dataset'  # Nome do dataset
    download_path = './data'  # Caminho onde os arquivos serão salvos
    dataset_file = os.path.join(download_path, 'pokemonall.csv')  # Arquivo que indica que o dataset foi baixado

    # Verifica se o arquivo do dataset já existe
    if os.path.exists(dataset_file):
        print("Dataset já baixado. Pulando o download.")
        return  # Sai da função, sem baixar novamente

    # Se o diretório não existir, cria
    if not os.path.exists(download_path):
        os.makedirs(download_path)

    print("Iniciando o download do dataset...")

    try:
        # Baixa o dataset do Kaggle
        kaggle.api.dataset_download_files(dataset, path=download_path, unzip=True)
        print(f"Dataset {dataset} baixado com sucesso!")
    except Exception as e:
        print(f"Ocorreu um erro ao baixar o dataset: {str(e)}")

if __name__ == "__main__":
    download_dataset()
