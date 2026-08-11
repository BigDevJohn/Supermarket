package jv.supermarket.shared;

import java.time.Instant;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jv.supermarket.shared.customexception.AlreadyExistException;
import jv.supermarket.shared.customexception.BadAuthRequestException;
import jv.supermarket.shared.customexception.ImageSavingException;
import jv.supermarket.shared.customexception.OutOfStockException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Mensagem> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String erro = "Recurso não encontrado";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);

    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Mensagem> alredyExist(AlreadyExistException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String erro = "Recurso já existe";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);

    }

    @ExceptionHandler(ImageSavingException.class)
    public ResponseEntity<Mensagem> salvamentoDeImagem(ImageSavingException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String erro = "Problemas com imagens";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro,request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Mensagem> argumentoInvalido(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String erro = "Problema ao validar valores: ";

        ArrayList<String> detalhes = new ArrayList<String>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            detalhes.add(fieldError.getDefaultMessage());
        }

        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro,request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Mensagem> estoqueInsuficiente(OutOfStockException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String erro = "Estoque insuficiente";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Mensagem> argumentoInvalido(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String erro = "Argumento inválido";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }
    
    @ExceptionHandler(BadAuthRequestException.class)
    public ResponseEntity<Mensagem> erroLogin(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String erro = "Erro com autenticação";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Mensagem> acessoNegado(AccessDeniedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String erro = "Erro ao acessar recurso";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Mensagem> redundanciaAoMudarDisponibilidade(IllegalStateException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String erro = "Mudança desnecessária de estado";
        ArrayList<String> detalhes = new ArrayList<String>();
        detalhes.add(e.getMessage());
        Mensagem mensagem = new Mensagem(Instant.now(), status.value(), erro, request.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(mensagem);
    }

    
}
