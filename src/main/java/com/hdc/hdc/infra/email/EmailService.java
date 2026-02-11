package com.hdc.hdc.infra.email;

import com.hdc.hdc.infra.email.interfaces.IEmailService;
import com.hdc.hdc.model.Usuario;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void enviarEmaildeCadastro(String nome, String email, String username, String senha) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Bem-vindo ao Hora de Cuidar");

            String html = """
                <html>
                <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#FFFFFF;">
                
                    <table align="center" width="600" cellpadding="0" cellspacing="0" 
                           style="background-color:#ffffff; margin-top:40px; border-radius:8px; padding:30px;">
                        
                        <tr>
                            <td align="center">
                                <img src='cid:logoImage' width="370" style="margin-bottom:20px;"/>
                            </td>
                        </tr>
                        
                        <tr>
                            <td>
                                <h2 style="color:#6699FF;">Olá %s,</h2>
                                
                                <p style="color:#555; font-size:14px;">
                                    Seja bem-vindo(a) ao <strong>Hora de Cuidar</strong>!
                                </p>
                                
                                <p style="color:#555; font-size:14px;">
                                    Seu cadastro foi realizado com sucesso na plataforma.
                                </p>
                                
                                <div style="background-color:#f1f3f6; padding:15px; border-radius:6px; margin:20px 0;">
                                    <p style="margin:5px 0;"><strong>Username:</strong> %s</p>
                                    <p style="margin:5px 0;"><strong>Senha:</strong> %s</p>
                                </div>
                                
                                <p style="color:#555; font-size:14px;">
                                    Recomendamos fortemente que você altere sua senha após o primeiro acesso.
                                </p>
                                
                                <p style="color:#555; font-size:14px;">
                                    O Hora de Cuidar é uma plataforma desenvolvida para o acompanhamento 
                                    eficiente de pacientes com doenças crônicas não transmissíveis, 
                                    promovendo organização, segurança e qualidade no cuidado.
                                </p>
                                
                                <div style="text-align:center; margin:30px 0;">
                                    <a href="http://localhost:8080/login"
                                       style="background-color:#6699FF; color:#ffffff; padding:12px 25px; 
                                              text-decoration:none; border-radius:5px; font-weight:bold;">
                                        Acessar Sistema
                                    </a>
                                </div>
                                
                                <p style="color:#999; font-size:12px; margin-top:30px;">
                                    Atenciosamente,<br/>
                                    <strong>Equipe Hora de Cuidar</strong>
                                </p>
                            </td>
                        </tr>
                        
                    </table>
                </body>
                </html>
                """.formatted(
                    nome,
                    username,
                    senha
            );

            helper.setText(html, true);

            ClassPathResource image = new ClassPathResource("static/logo-hdc.png");
            helper.addInline("logoImage", image);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de cadastro", e);
        }
    }
}