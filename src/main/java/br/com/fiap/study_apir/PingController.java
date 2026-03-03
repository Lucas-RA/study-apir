package br.com.fiap.study_apir;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// responsável por ler as anotações 
import org.springframework.web.bind.annotation.RestController;

// anotação - adiciona uma funcionalidade na classe
//visualizar todas as requisições que vierem como mapa
@RestController
@RequestMapping("") ///base - essa controller - "/base" - para os outros /base/...>> Sem nada, fica na raiz /
public class PingController {

    // assim só a / retorna 
    @GetMapping()
    public String raiz(){
        return "raiz";
    }
// assim ficaria /base/ping 
    // dizendo que o /ping está associado neste método
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

    @GetMapping("/rota1")
    public String rota1(){
        return "rota1";
    }
}
