-- dialect: mysql

Insert Into mensagens
(`role`, `content`, `data_hora`, `usuario_id`)
Values
('USER', 'Olá? Tudo Bem?','2026-05-01 10:30:00', 1),
('SYSTEM', 'Tudo certo! Como Vai?', '2026-05-01 10:30:01', 1),
('USER', '.', '2026-06-02 11:00:04', 2),
('SYSTEM', 'Olá? Sobre o que gostaria de conversar?', '2026-06-02 11:00:05', 2),
('USER', 'Poderia me falar sobre os planos de saúde disponíveis?', '2026-06-02 11:00:06', 2),
('SYSTEM', 'Claro que sim! Aqui vai uma lista de planos disponíveis da Care Plus:\n- Care Plus Soho(Para 10 a 29 vidas)\n- Clube Care Plus(Para 30 a 200 vidas)\n- Care Plus Empresarial(Para mais de 200 vidas)', '2026-06-02 11:00:07', 2),
('USER', 'Oi', '2026-08-03 15:30:11', 3),
('SYSTEM', 'Oi! Em que poderia ajudar?', '2026-08-03 15:30:12', 3);