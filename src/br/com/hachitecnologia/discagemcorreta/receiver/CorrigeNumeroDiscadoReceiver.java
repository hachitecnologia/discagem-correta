package br.com.hachitecnologia.discagemcorreta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Broadcast Receiver responsável por corrigir um número discado 
 * no formato: 
 * 		0 + Código DDD + Número do Telefone (exemplo: 096 8765-4321)
 * para o seguinte formato:
 * 		Código da operadora + Código DDD + Número do Telefone (exemplo: 0 41 96 8765-4321)
 * @author lucasfreitas
 *
 */
public class CorrigeNumeroDiscadoReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
         
        /**
         * Tentamos obter o número discado através de algum outro 
         * Broadcast Receiver que já tenha sido executado antes do nosso.
         */
        String numeroDiscado = getResultData();
        /**
         * Caso não tenhamos conseguido esta informação através de outro 
         * Broadcast Receiver que tenha sido executado anteriormente, obtemos 
         * o número discado através da Intent original que lançou o broadcast.
         * No Android, esta informação é obtida através de uma String enviada 
         * na Intent, com o identificador: EXTRA_PHONE_NUMBER
         */
        if (numeroDiscado == null)
            numeroDiscado = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
         
        // Obtemos o acesso às Preferências definidas pelo usuário 
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        // Formataremos o número discado apenas se o usuário definiu o aplicativo como Ativo
        boolean ativo = preferencias.getBoolean("codigo_longa_distancia_ativo", false);
        if (ativo) {
            // Obtemos o código da operadora definido pelo usuário na tela de Preferências
            String codigoLongaDistancia = preferencias.getString("codigo_longa_distancia", "");
            /**
             *  Passamos o número já formatado para o Broadcast Receiver do Android 
             *  que irá ativar o mecanismo para efetuar a nova chamada telefônica.
             */
            setResultData(formataNumero(numeroDiscado, codigoLongaDistancia));
        }
 
         
    }
     
    /**
     * Método responsável por formatar o número a ser discado, acrescentando 
     * a ele o código de longa distância da operadora.
     * @param numero
     * @return
     */
    private String formataNumero(String numero, String codigoLongaDistancia) {
        /**
         * Formataremos apenas os números discados que casam com o seguinte padrão:
         *  . Números iniciados com 0 (zero)
         *  . Números que contenham 11 dígitos (Exemplo: 09687654321)
         */
        if (numero.startsWith("0") && numero.length() == 11) {
            // Obtemos o código DDD do número discado
            String ddd = numero.substring(1, 3);
            /**
             *  Retornamos o número no padrão: 
             *  0 + Código da Operadora + Código DDD + Número do Telefone 
             */
            return "0" + codigoLongaDistancia + ddd + numero.substring(3);
        }
        // Caso o padrão não se aplique, apenas retornamos o número discado original
        return numero;
    }
 
}