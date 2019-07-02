-- This SQL file contains some example data
-- it is loaded when Spring starts in dev mode with embedded H2 db

INSERT INTO Categories (`id`, `name`, `parent_id`) VALUES
(11, 'Semiconductors', null),
(22, 'Transistors', 11),
(33, 'Elem pasywne', null);

INSERT INTO Items (id, name, quantity, description, website, category_id) VALUES
(123, 'bc327', 1, 'tranzystor pnp', null, 22),
(124, 'irlz44n', 6, 'mosfet n', null, 22),
(125, 'stm32f103', 2, 'mikrokontroler arm', 'https://st.com', 11),
(126, '100 ohm 0.25W', 700, 'rezystor tht', null, 33);