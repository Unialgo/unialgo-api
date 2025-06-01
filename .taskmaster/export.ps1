$OutputPath = ".\export_para_llm_detalhado.txt"
$Separator = "---END_OF_FILE_CONTENT---" # Marcador claro para a LLM

Clear-Content -Path $OutputPath -ErrorAction SilentlyContinue

Get-ChildItem -Path . -Recurse -File | ForEach-Object {
    Add-Content -Path $OutputPath -Value "--- ARQUIVO INÍCIO: $($_.FullName) ---"
    Add-Content -Path $OutputPath -Value (Get-Content -Path $_.FullName -Raw)
    Add-Content -Path $OutputPath -Value "$Separator" # Marcador de fim de arquivo
    Add-Content -Path $OutputPath -Value "" # Linha em branco
}

Write-Host "Exportação detalhada concluída para: $OutputPath"
Invoke-Item $OutputPath