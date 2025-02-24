import pandas as pd

# Carregar o arquivo CSV
df = pd.read_csv("/content/All_Pokemon.csv")

# Listar as colunas a serem removidas
colunas_para_remover = [
    "Against Normal", "Against Fire", "Against Water", "Against Electric", "Against Grass", 
    "Against Ice", "Against Fighting", "Against Poison", "Against Ground", "Against Flying", 
    "Against Psychic", "Against Bug", "Against Rock", "Against Ghost", "Alolan Form","Against Dragon", 
    "Against Dark", "Against Steel", "Against Fairy", "Final Evolution","Galarian Form",
    "Experience to level 100", "Experience type"
]

# Remover as colunas do DataFrame
df = df.drop(columns=colunas_para_remover)

# Salvar o novo arquivo CSV sem as colunas removidas
df.to_csv("dados_modificados.csv", index=False)

print("Colunas removidas com sucesso!") 
import pandas as pd

# Dicionário com as gerações e suas datas de lançamento
geracoes = {
    "1": "01/01/1996",  # Geração 1 (Kanto)
    "2": "21/11/1999",  # Geração 2 (Johto)
    "3": "21/11/2002",  # Geração 3 (Hoenn)
    "4": "22/09/2006",  # Geração 4 (Sinnoh)
    "5": "18/09/2010",  # Geração 5 (Unova)
    "6": "12/10/2013",  # Geração 6 (Kalos)
    "7": "18/11/2016",  # Geração 7 (Alola)
    "8": "15/11/2019",  # Geração 8 (Galar)
    "9": "18/11/2022"   # Geração 9 (Paldea) - Se disponível
}

# Carregar o CSV no pandas
df = pd.read_csv('/content/dados_modificados.csv')

# Transformar a coluna 'Generation' em inteiros para mapear corretamente as datas
df['Generation'] = df['Generation'].astype(int).astype(str)

# Substituir os valores da coluna 'Generation' pelas datas
df['Generation'] = df['Generation'].map(geracoes).fillna("Data não disponível")

# Salvar o DataFrame modificado em um novo arquivo CSV
df.to_csv('dados_modificados.csv', index=False)

print("Arquivo CSV modificado com sucesso!")
