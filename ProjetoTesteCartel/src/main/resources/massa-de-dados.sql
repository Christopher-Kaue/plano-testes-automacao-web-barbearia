-- =====================================================================
-- MASSA DE DADOS DOS TESTES AUTOMATIZADOS - Cartel dos Cortes
-- Roda antes da suite (BaseTest.prepararBanco), SO no banco "..._teste".
-- Apaga tudo e recria um cenario conhecido, para os testes sempre
-- comecarem do mesmo ponto (independente de execucoes anteriores).
-- As datas sao relativas ao dia de hoje no fuso de Sao Paulo.
-- =====================================================================

-- 1) Limpa todas as tabelas e reinicia os ids (o primeiro registro volta a ser 1).
DO $$
DECLARE r record;
BEGIN
    FOR r IN SELECT tablename FROM pg_tables WHERE schemaname = 'public' LOOP
        EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
    END LOOP;
END $$;

-- 2) Servicos (ids 1, 2 e 3)
INSERT INTO servico (nome, descricao, duracao_min, preco) VALUES
    ('Corte degradê', 'Degradê na máquina com acabamento na navalha.', 40, 30.00),
    ('Sobrancelha', 'Limpeza e alinhamento na navalha.', 15, 15.00),
    ('Degradê + barba', 'Corte degradê com barba modelada na navalha.', 60, 50.00);

-- 3) Barbeiros (ids 1 e 2), os dois no salao 1 (Mariante) e fazendo todos os servicos
INSERT INTO barbeiro (nome, salao_id, saloes) VALUES
    ('Barbeiro Um', 1, ARRAY[1]::smallint[]),
    ('Barbeiro Dois', 1, ARRAY[1]::smallint[]);

INSERT INTO barbeiro_servico (barbeiro_id, servico_id)
SELECT b.id, s.id FROM barbeiro b CROSS JOIN servico s;

-- 4) Expediente: todos os dias, 09:00 as 19:00 (o teste funciona em qualquer dia da semana)
INSERT INTO expediente (dia_semana, abre, fecha)
SELECT d, TIME '09:00', TIME '19:00' FROM generate_series(0, 6) AS d;

-- 5) Folga avulsa do Barbeiro Dois AMANHA (caso CT02.4)
INSERT INTO folga (barbeiro_id, data)
VALUES (2, (now() AT TIME ZONE 'America/Sao_Paulo')::date + 1);

-- 6) Agendamentos prontos para os cenarios CT03 e CT04
--    Os codigos do link do cliente sao fixos para o teste abrir /agendamento/<codigo>.
INSERT INTO agendamento (barbeiro_id, servico_id, salao_id, cliente_nome, cliente_telefone, inicio, fim, preco, forma_pagamento, codigo)
SELECT v.barbeiro, 1, 1, v.nome, v.telefone, v.inicio, v.inicio + INTERVAL '40 minutes', 30.00, v.forma, v.codigo::uuid
FROM (
    SELECT (now() AT TIME ZONE 'America/Sao_Paulo')::date AS hoje,
           date_trunc('minute', now() AT TIME ZONE 'America/Sao_Paulo') AS agora
) t
CROSS JOIN LATERAL (VALUES
    -- id 1: CT03.1 cliente cancela pelo link (daqui a 3 dias)
    (1, 'Cliente Cancela',   '51911110001', t.hoje + 3 + TIME '10:00', 'dinheiro', '00000000-0000-0000-0000-000000000001'),
    -- id 2: CT03.2 cliente troca a forma de pagamento
    (1, 'Cliente Troca',     '51911110002', t.hoje + 3 + TIME '11:00', 'dinheiro', '00000000-0000-0000-0000-000000000002'),
    -- id 3: CT03.3 cliente desiste de cancelar (clica Voltar)
    (1, 'Cliente Desiste',   '51911110003', t.hoje + 3 + TIME '12:00', 'dinheiro', '00000000-0000-0000-0000-000000000003'),
    -- id 4: CT03.4 faltam menos de 60 minutos: nao pode mais cancelar pelo site
    (2, 'Cliente Em Cima',   '51911110004', t.agora + INTERVAL '30 minutes', 'dinheiro', '00000000-0000-0000-0000-000000000004'),
    -- id 5: CT04.1 receber em uma forma so (Pix)
    (1, 'Cliente Pix',       '51922220001', t.hoje + TIME '06:00', NULL, '00000000-0000-0000-0000-000000000005'),
    -- id 6: CT04.2 receber dividido (dinheiro + Pix)
    (1, 'Cliente Dividido',  '51922220002', t.hoje + TIME '06:45', NULL, '00000000-0000-0000-0000-000000000006'),
    -- id 7: CT04.3 valores divididos passam do total
    (1, 'Cliente Passou',    '51922220003', t.hoje + TIME '07:30', NULL, '00000000-0000-0000-0000-000000000007')
) AS v (barbeiro, nome, telefone, inicio, forma, codigo)
ORDER BY v.codigo;
