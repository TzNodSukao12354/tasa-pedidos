--
-- PostgreSQL database dump
--

\restrict AePRsqUmMmldRGwuZ0Yq1YQt3dTE8Qsc9AVDIFwIKeifyVlkZfai5aFKdHDpaHK

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: almacen; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.almacen (
    idalmacen integer NOT NULL,
    nombre character varying(100) NOT NULL,
    direccion character varying(255),
    capacidad numeric(10,2),
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    CONSTRAINT chk_almacen_estado CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying])::text[])))
);


ALTER TABLE public.almacen OWNER TO postgres;

--
-- Name: almacen_idalmacen_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.almacen_idalmacen_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.almacen_idalmacen_seq OWNER TO postgres;

--
-- Name: almacen_idalmacen_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.almacen_idalmacen_seq OWNED BY public.almacen.idalmacen;


--
-- Name: auditoria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.auditoria (
    idauditoria integer NOT NULL,
    idusuario integer,
    tablaafectada character varying(50) NOT NULL,
    accion character varying(20) NOT NULL,
    detalle text,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_auditoria_accion CHECK (((accion)::text = ANY ((ARRAY['INSERT'::character varying, 'UPDATE'::character varying, 'DELETE'::character varying, 'LOGIN'::character varying, 'LOGOUT'::character varying])::text[])))
);


ALTER TABLE public.auditoria OWNER TO postgres;

--
-- Name: auditoria_idauditoria_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.auditoria_idauditoria_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.auditoria_idauditoria_seq OWNER TO postgres;

--
-- Name: auditoria_idauditoria_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.auditoria_idauditoria_seq OWNED BY public.auditoria.idauditoria;


--
-- Name: chofer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chofer (
    idchofer integer NOT NULL,
    dni character varying(8) NOT NULL,
    nombre character varying(100) NOT NULL,
    licencia character varying(20) NOT NULL,
    telefono character varying(20),
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    CONSTRAINT chk_chofer_estado CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying])::text[])))
);


ALTER TABLE public.chofer OWNER TO postgres;

--
-- Name: chofer_idchofer_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chofer_idchofer_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.chofer_idchofer_seq OWNER TO postgres;

--
-- Name: chofer_idchofer_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chofer_idchofer_seq OWNED BY public.chofer.idchofer;


--
-- Name: detallepedido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.detallepedido (
    iddetalle integer NOT NULL,
    idpedido integer NOT NULL,
    idproducto integer NOT NULL,
    cantidad integer NOT NULL,
    preciounitario numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    CONSTRAINT chk_det_cantidad CHECK ((cantidad > 0)),
    CONSTRAINT chk_det_subtotal CHECK ((subtotal >= (0)::numeric))
);


ALTER TABLE public.detallepedido OWNER TO postgres;

--
-- Name: detallepedido_iddetalle_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.detallepedido_iddetalle_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.detallepedido_iddetalle_seq OWNER TO postgres;

--
-- Name: detallepedido_iddetalle_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.detallepedido_iddetalle_seq OWNED BY public.detallepedido.iddetalle;


--
-- Name: empresa; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.empresa (
    idempresa integer NOT NULL,
    ruc character varying(11) NOT NULL,
    razonsocial character varying(150) NOT NULL,
    telefono character varying(20),
    direccion character varying(255),
    correo character varying(100),
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    CONSTRAINT chk_empresa_estado CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying])::text[])))
);


ALTER TABLE public.empresa OWNER TO postgres;

--
-- Name: empresa_idempresa_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.empresa_idempresa_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.empresa_idempresa_seq OWNER TO postgres;

--
-- Name: empresa_idempresa_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.empresa_idempresa_seq OWNED BY public.empresa.idempresa;


--
-- Name: guiadespacho; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.guiadespacho (
    idguia integer NOT NULL,
    idpedido integer NOT NULL,
    idvehiculo integer NOT NULL,
    idchofer integer NOT NULL,
    idruta integer NOT NULL,
    fechasalida date NOT NULL,
    fechaentrega date,
    estado character varying(30) DEFAULT 'GENERADA'::character varying NOT NULL,
    observaciones text,
    horasalidareal timestamp without time zone,
    CONSTRAINT chk_guia_estado CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE_ACEPTACION'::character varying, 'ACEPTADA'::character varying, 'RECHAZADA'::character varying, 'GENERADA'::character varying, 'EN_TRANSITO'::character varying, 'ENTREGADA'::character varying, 'ANULADA'::character varying])::text[])))
);


ALTER TABLE public.guiadespacho OWNER TO postgres;

--
-- Name: guiadespacho_idguia_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.guiadespacho_idguia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.guiadespacho_idguia_seq OWNER TO postgres;

--
-- Name: guiadespacho_idguia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.guiadespacho_idguia_seq OWNED BY public.guiadespacho.idguia;


--
-- Name: inventario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventario (
    idinventario integer NOT NULL,
    idproducto integer NOT NULL,
    idalmacen integer NOT NULL,
    tipomovimiento character varying(20) NOT NULL,
    cantidad integer NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    motivo character varying(255),
    CONSTRAINT chk_inv_cantidad CHECK ((cantidad > 0)),
    CONSTRAINT chk_inv_tipo CHECK (((tipomovimiento)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SALIDA'::character varying, 'AJUSTE'::character varying])::text[])))
);


ALTER TABLE public.inventario OWNER TO postgres;

--
-- Name: inventario_idinventario_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inventario_idinventario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inventario_idinventario_seq OWNER TO postgres;

--
-- Name: inventario_idinventario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inventario_idinventario_seq OWNED BY public.inventario.idinventario;


--
-- Name: notificacion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notificacion (
    idnotificacion integer NOT NULL,
    idusuario integer NOT NULL,
    titulo character varying(100) NOT NULL,
    mensaje text NOT NULL,
    leido boolean DEFAULT false,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.notificacion OWNER TO postgres;

--
-- Name: notificacion_idnotificacion_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.notificacion_idnotificacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notificacion_idnotificacion_seq OWNER TO postgres;

--
-- Name: notificacion_idnotificacion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.notificacion_idnotificacion_seq OWNED BY public.notificacion.idnotificacion;


--
-- Name: pedido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pedido (
    idpedido integer NOT NULL,
    idempresa integer NOT NULL,
    idusuario integer NOT NULL,
    fechapedido timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fechaentrega date,
    estado character varying(30) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    total numeric(10,2) DEFAULT 0.00 NOT NULL,
    observaciones text,
    CONSTRAINT chk_pedido_estado CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'CONFIRMADO'::character varying, 'EN_DESPACHO'::character varying, 'ENTREGADO'::character varying, 'ANULADO'::character varying])::text[]))),
    CONSTRAINT chk_pedido_total CHECK ((total >= (0)::numeric))
);


ALTER TABLE public.pedido OWNER TO postgres;

--
-- Name: pedido_idpedido_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pedido_idpedido_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pedido_idpedido_seq OWNER TO postgres;

--
-- Name: pedido_idpedido_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pedido_idpedido_seq OWNED BY public.pedido.idpedido;


--
-- Name: producto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.producto (
    idproducto integer NOT NULL,
    codigo character varying(30) NOT NULL,
    nombre character varying(150) NOT NULL,
    descripcion text,
    unidadmedida character varying(20) NOT NULL,
    precio numeric(10,2) DEFAULT 0.00 NOT NULL,
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    CONSTRAINT chk_producto_estado CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying])::text[]))),
    CONSTRAINT chk_producto_precio CHECK ((precio >= (0)::numeric))
);


ALTER TABLE public.producto OWNER TO postgres;

--
-- Name: producto_idproducto_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.producto_idproducto_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.producto_idproducto_seq OWNER TO postgres;

--
-- Name: producto_idproducto_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.producto_idproducto_seq OWNED BY public.producto.idproducto;


--
-- Name: rol; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rol (
    idrol integer NOT NULL,
    nombre character varying(50) NOT NULL,
    descripcion character varying(255)
);


ALTER TABLE public.rol OWNER TO postgres;

--
-- Name: rol_idrol_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rol_idrol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rol_idrol_seq OWNER TO postgres;

--
-- Name: rol_idrol_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rol_idrol_seq OWNED BY public.rol.idrol;


--
-- Name: ruta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ruta (
    idruta integer NOT NULL,
    nombre character varying(100) NOT NULL,
    zona character varying(100) NOT NULL,
    distancia numeric(10,2) NOT NULL,
    CONSTRAINT chk_ruta_distancia CHECK ((distancia > (0)::numeric))
);


ALTER TABLE public.ruta OWNER TO postgres;

--
-- Name: ruta_idruta_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ruta_idruta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ruta_idruta_seq OWNER TO postgres;

--
-- Name: ruta_idruta_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ruta_idruta_seq OWNED BY public.ruta.idruta;


--
-- Name: ticketentrega; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ticketentrega (
    idticket integer NOT NULL,
    idguia integer NOT NULL,
    fechageneracion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    nombrereceptor character varying(150),
    firmaevidencia character varying(255),
    observaciones text,
    estado character varying(30) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    CONSTRAINT chk_ticket_estado CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'CONFIRMADO'::character varying, 'RECHAZADO'::character varying])::text[])))
);


ALTER TABLE public.ticketentrega OWNER TO postgres;

--
-- Name: ticketentrega_idticket_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ticketentrega_idticket_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ticketentrega_idticket_seq OWNER TO postgres;

--
-- Name: ticketentrega_idticket_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ticketentrega_idticket_seq OWNED BY public.ticketentrega.idticket;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    idusuario integer NOT NULL,
    nombre character varying(100) NOT NULL,
    correo character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    telefono character varying(20),
    estado character varying(20) DEFAULT 'ACTIVO'::character varying NOT NULL,
    idrol integer NOT NULL,
    fechacreacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    idempresa integer,
    CONSTRAINT chk_usuario_estado CHECK (((estado)::text = ANY ((ARRAY['ACTIVO'::character varying, 'INACTIVO'::character varying])::text[])))
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- Name: usuario_idusuario_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuario_idusuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuario_idusuario_seq OWNER TO postgres;

--
-- Name: usuario_idusuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuario_idusuario_seq OWNED BY public.usuario.idusuario;


--
-- Name: vehiculo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehiculo (
    idvehiculo integer NOT NULL,
    placa character varying(10) NOT NULL,
    marca character varying(50),
    modelo character varying(50),
    capacidad numeric(10,2) NOT NULL,
    estado character varying(20) DEFAULT 'DISPONIBLE'::character varying NOT NULL,
    CONSTRAINT chk_vehiculo_estado CHECK (((estado)::text = ANY ((ARRAY['DISPONIBLE'::character varying, 'EN_RUTA'::character varying, 'MANTENIMIENTO'::character varying])::text[])))
);


ALTER TABLE public.vehiculo OWNER TO postgres;

--
-- Name: vehiculo_idvehiculo_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vehiculo_idvehiculo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vehiculo_idvehiculo_seq OWNER TO postgres;

--
-- Name: vehiculo_idvehiculo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vehiculo_idvehiculo_seq OWNED BY public.vehiculo.idvehiculo;


--
-- Name: almacen idalmacen; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.almacen ALTER COLUMN idalmacen SET DEFAULT nextval('public.almacen_idalmacen_seq'::regclass);


--
-- Name: auditoria idauditoria; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditoria ALTER COLUMN idauditoria SET DEFAULT nextval('public.auditoria_idauditoria_seq'::regclass);


--
-- Name: chofer idchofer; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chofer ALTER COLUMN idchofer SET DEFAULT nextval('public.chofer_idchofer_seq'::regclass);


--
-- Name: detallepedido iddetalle; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallepedido ALTER COLUMN iddetalle SET DEFAULT nextval('public.detallepedido_iddetalle_seq'::regclass);


--
-- Name: empresa idempresa; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empresa ALTER COLUMN idempresa SET DEFAULT nextval('public.empresa_idempresa_seq'::regclass);


--
-- Name: guiadespacho idguia; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho ALTER COLUMN idguia SET DEFAULT nextval('public.guiadespacho_idguia_seq'::regclass);


--
-- Name: inventario idinventario; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario ALTER COLUMN idinventario SET DEFAULT nextval('public.inventario_idinventario_seq'::regclass);


--
-- Name: notificacion idnotificacion; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificacion ALTER COLUMN idnotificacion SET DEFAULT nextval('public.notificacion_idnotificacion_seq'::regclass);


--
-- Name: pedido idpedido; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido ALTER COLUMN idpedido SET DEFAULT nextval('public.pedido_idpedido_seq'::regclass);


--
-- Name: producto idproducto; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto ALTER COLUMN idproducto SET DEFAULT nextval('public.producto_idproducto_seq'::regclass);


--
-- Name: rol idrol; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rol ALTER COLUMN idrol SET DEFAULT nextval('public.rol_idrol_seq'::regclass);


--
-- Name: ruta idruta; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ruta ALTER COLUMN idruta SET DEFAULT nextval('public.ruta_idruta_seq'::regclass);


--
-- Name: ticketentrega idticket; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticketentrega ALTER COLUMN idticket SET DEFAULT nextval('public.ticketentrega_idticket_seq'::regclass);


--
-- Name: usuario idusuario; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario ALTER COLUMN idusuario SET DEFAULT nextval('public.usuario_idusuario_seq'::regclass);


--
-- Name: vehiculo idvehiculo; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehiculo ALTER COLUMN idvehiculo SET DEFAULT nextval('public.vehiculo_idvehiculo_seq'::regclass);


--
-- Data for Name: almacen; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.almacen (idalmacen, nombre, direccion, capacidad, estado) FROM stdin;
1	Almacén Central Chimbote	Av. Industrial 500, Chimbote	500.00	ACTIVO
\.


--
-- Data for Name: auditoria; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.auditoria (idauditoria, idusuario, tablaafectada, accion, detalle, fecha) FROM stdin;
11	2	Pedido	INSERT	Pedido #6 registrado. Total: S/ 3620.50	2026-06-11 14:49:46.51536
12	2	Pedido	INSERT	Pedido #7 registrado. Total: S/ 6908.50	2026-06-11 15:00:36.941036
13	2	Pedido	INSERT	Pedido #8 registrado. Total: S/ 21228.50	2026-06-11 15:22:11.132374
14	2	Pedido	INSERT	Pedido #9 registrado. Total: S/ 6908.50	2026-06-11 16:12:29.376641
15	3	Pedido	INSERT	Pedido #10 registrado. Total: S/ 3363.50	2026-06-11 18:29:08.386911
16	3	Pedido	INSERT	Pedido #11 registrado. Total: S/ 9900.00	2026-06-11 18:41:24.781053
17	3	Pedido	INSERT	Pedido #12 registrado. Total: S/ 3363.50	2026-06-11 20:48:10.464554
18	3	Pedido	INSERT	Pedido #13 registrado. Total: S/ 10090.50	2026-06-11 22:27:14.181311
19	3	Pedido	INSERT	Pedido #14 registrado. Total: S/ 3300.00	2026-06-12 18:13:56.814288
20	3	Pedido	INSERT	Pedido #15 registrado. Total: S/ 7070.00	2026-06-12 20:02:47.34798
21	4	Pedido	INSERT	Pedido #16 registrado. Total: S/ 5735.00	2026-06-12 21:16:34.33866
22	7	Pedido	UPDATE	Pedido #7 → CONFIRMADO	2026-06-23 20:51:58.440749
23	7	Pedido	UPDATE	Pedido #7 → EN_DESPACHO	2026-06-23 20:58:40.692482
24	7	Pedido	UPDATE	Pedido #9 → CONFIRMADO	2026-06-23 21:43:39.379231
25	7	Pedido	UPDATE	Pedido #9 → EN_DESPACHO	2026-06-23 21:44:00.868883
26	8	GuiaDespacho	UPDATE	Chofer Luis Mendoza Torres ACEPTÓ la guía #2	2026-06-23 22:08:56.729389
27	7	Pedido	UPDATE	Pedido #10 → CONFIRMADO	2026-06-23 22:29:38.554239
28	7	Pedido	UPDATE	Pedido #10 — Guía asignada a Carlos Pérez Gonzales, esperando aceptación	2026-06-23 22:29:50.088653
29	9	GuiaDespacho	UPDATE	Chofer Carlos Pérez Gonzales ACEPTÓ la guía #3	2026-06-23 22:46:12.255638
30	9	GuiaDespacho	UPDATE	Chofer Carlos Pérez Gonzales RECHAZÓ la guía #1	2026-06-23 22:46:46.762799
\.


--
-- Data for Name: chofer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chofer (idchofer, dni, nombre, licencia, telefono, estado) FROM stdin;
1	43123456	Carlos Pérez Gonzales	A-IIb	943123456	ACTIVO
2	41987654	Luis Mendoza Torres	A-IIb	941987654	ACTIVO
3	45112233	Jorge Ramírez Castillo	A-IIIc	945112233	ACTIVO
4	40556677	Pedro Vásquez Lima	A-IIb	940556677	ACTIVO
\.


--
-- Data for Name: detallepedido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.detallepedido (iddetalle, idpedido, idproducto, cantidad, preciounitario, subtotal) FROM stdin;
11	6	8	3	28.50	85.50
12	6	9	1	35.00	35.00
13	6	4	1	1650.00	1650.00
14	6	3	1	1850.00	1850.00
15	7	5	1	1200.00	1200.00
16	7	6	1	2100.00	2100.00
17	7	8	1	28.50	28.50
18	7	9	1	35.00	35.00
19	7	4	1	1650.00	1650.00
20	7	3	1	1850.00	1850.00
21	7	7	1	45.00	45.00
22	8	5	1	1200.00	1200.00
23	8	6	1	2100.00	2100.00
24	8	8	1	28.50	28.50
25	8	9	5	35.00	175.00
26	8	4	5	1650.00	8250.00
27	8	3	5	1850.00	9250.00
28	8	7	5	45.00	225.00
29	9	5	1	1200.00	1200.00
30	9	6	1	2100.00	2100.00
31	9	8	1	28.50	28.50
32	9	9	1	35.00	35.00
33	9	4	1	1650.00	1650.00
34	9	3	1	1850.00	1850.00
35	9	7	1	45.00	45.00
36	10	5	1	1200.00	1200.00
37	10	6	1	2100.00	2100.00
38	10	8	1	28.50	28.50
39	10	9	1	35.00	35.00
40	11	5	3	1200.00	3600.00
41	11	6	3	2100.00	6300.00
42	12	5	1	1200.00	1200.00
43	12	6	1	2100.00	2100.00
44	12	8	1	28.50	28.50
45	12	9	1	35.00	35.00
46	13	5	3	1200.00	3600.00
47	13	6	3	2100.00	6300.00
48	13	8	3	28.50	85.50
49	13	9	3	35.00	105.00
50	14	5	1	1200.00	1200.00
51	14	6	1	2100.00	2100.00
52	15	9	2	35.00	70.00
53	15	4	2	1650.00	3300.00
54	15	3	2	1850.00	3700.00
55	16	5	2	1200.00	2400.00
56	16	9	1	35.00	35.00
57	16	4	2	1650.00	3300.00
\.


--
-- Data for Name: empresa; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.empresa (idempresa, ruc, razonsocial, telefono, direccion, correo, estado) FROM stdin;
1	20100000001	Distribuciones Lima S.A.C.	01-4445566	Av. Industrial 123, Lima	contacto@distrilima.pe	ACTIVO
2	20100000002	Mi Empresa S.A.C.	999888777	Av. Lima 123	raznakrotherrol@gmail.com	ACTIVO
3	12345678901	Pescados	964513575	A.V Chimbote	samiremilianorosaleshuanca6@gmail.com	ACTIVO
4	20456789123	Pesquera Norte SAC	987654321	Av José Parde 1250, Chimbote	huancaemiliano467@gmail.com	ACTIVO
\.


--
-- Data for Name: guiadespacho; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.guiadespacho (idguia, idpedido, idvehiculo, idchofer, idruta, fechasalida, fechaentrega, estado, observaciones, horasalidareal) FROM stdin;
2	9	2	2	1	2026-06-30	\N	ACEPTADA	\N	\N
3	10	3	1	6	2026-06-23	\N	ACEPTADA	\N	\N
1	7	1	1	1	2026-06-24	\N	RECHAZADA	\N	\N
\.


--
-- Data for Name: inventario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventario (idinventario, idproducto, idalmacen, tipomovimiento, cantidad, fecha, motivo) FROM stdin;
5	5	1	ENTRADA	1200	2026-06-23 23:55:27.896157	Registrado por Encargado Almacén
6	6	1	ENTRADA	2400	2026-06-23 23:55:36.082658	Registrado por Encargado Almacén
7	6	1	SALIDA	1200	2026-06-23 23:55:40.946476	Registrado por Encargado Almacén
8	8	1	ENTRADA	20	2026-06-23 23:55:47.457309	Registrado por Encargado Almacén
9	8	1	SALIDA	18	2026-06-23 23:55:51.29045	Registrado por Encargado Almacén
10	8	1	SALIDA	1	2026-06-24 00:01:12.53755	Registrado por Encargado Almacén
\.


--
-- Data for Name: notificacion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.notificacion (idnotificacion, idusuario, titulo, mensaje, leido, fecha) FROM stdin;
\.


--
-- Data for Name: pedido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pedido (idpedido, idempresa, idusuario, fechapedido, fechaentrega, estado, total, observaciones) FROM stdin;
8	2	2	2026-06-11 15:22:11.115784	2026-06-12	ANULADO	21228.50	
6	1	2	2026-06-11 14:49:46.491771	2026-06-11	ANULADO	3620.50	
11	3	3	2026-06-11 18:41:24.759562	2026-06-11	PENDIENTE	9900.00	
12	3	3	2026-06-11 20:48:10.39938	2026-06-11	PENDIENTE	3363.50	
13	3	3	2026-06-11 22:27:14.158119	2026-06-12	ANULADO	10090.50	
14	3	3	2026-06-12 18:13:56.779935	2026-06-12	ANULADO	3300.00	Destino: Av cerro blanco — Zona: Trujillo — Obs: vsfsfs
15	3	3	2026-06-12 20:02:47.319057	2026-06-15	PENDIENTE	7070.00	Destino: Avbthbtuy — Zona: Huacambo
16	4	4	2026-06-12 21:16:34.319224	2026-06-15	PENDIENTE	5735.00	Destino: Av. Industrial 123 — Zona: Huacambo
7	1	2	2026-06-11 15:00:36.917086	2026-06-18	EN_DESPACHO	6908.50	Lo quiero ahorita porque subire al 0,2% 
9	2	2	2026-06-11 16:12:29.363003	2026-06-11	EN_DESPACHO	6908.50	
10	3	3	2026-06-11 18:29:08.370886	2026-06-11	EN_DESPACHO	3363.50	
\.


--
-- Data for Name: producto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.producto (idproducto, codigo, nombre, descripcion, unidadmedida, precio, estado) FROM stdin;
3	TASA-001	Harina de Pescado Prime	Harina de pescado alta proteína 67% - Exportación	TM	1850.00	ACTIVO
4	TASA-002	Harina de Pescado FAQ	Harina de pescado estándar 65% proteína	TM	1650.00	ACTIVO
5	TASA-003	Aceite de Pescado Crudo	Aceite de pescado crudo para industria	TM	1200.00	ACTIVO
6	TASA-004	Aceite de Pescado Refinado	Aceite refinado para consumo humano	TM	2100.00	ACTIVO
7	TASA-005	Omega 3 Concentrado	Omega 3 concentrado para farmacéutica	KG	45.00	ACTIVO
8	TASA-006	Anchoveta Tiki Entero	Anchoveta entera para consumo humano	CAJA	28.50	ACTIVO
9	TASA-007	Anchoveta Tiki Filete	Filete de anchoveta en aceite vegetal	CAJA	35.00	ACTIVO
\.


--
-- Data for Name: rol; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rol (idrol, nombre, descripcion) FROM stdin;
1	ADMIN	Acceso total al sistema
2	VENDEDOR	Registro y seguimiento de pedidos
3	ALMACEN	Gestión de inventario y despacho
4	CHOFER	Visualización de guías de despacho asignadas
\.


--
-- Data for Name: ruta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ruta (idruta, nombre, zona, distancia) FROM stdin;
1	Ruta Chimbote Centro	Chimbote	5.00
2	Ruta Nuevo Chimbote	Nuevo Chimbote	8.00
3	Ruta Trujillo	Trujillo	130.00
4	Ruta Samanco	Samanco	45.00
5	Ruta Huacambo	Huacambo	35.00
6	Ruta Capellanía	Capellanía	20.00
7	Ruta Nepeña	Nepeña	25.00
8	Ruta San Jacinto	San Jacinto	30.00
9	Ruta Moro	Moro	55.00
10	Ruta Jimbe	Jimbe	60.00
\.


--
-- Data for Name: ticketentrega; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ticketentrega (idticket, idguia, fechageneracion, nombrereceptor, firmaevidencia, observaciones, estado) FROM stdin;
\.


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuario (idusuario, nombre, correo, password, telefono, estado, idrol, fechacreacion, idempresa) FROM stdin;
2	Acuña	raznakrotherrol@gmail.com	123456	999888777	ACTIVO	2	2026-06-11 14:12:57.410277	2
3	Emiliano	samiremilianorosaleshuanca6@gmail.com	12354	964513575	ACTIVO	2	2026-06-11 15:40:58.471291	3
4	Juan Perez Diaz	huancaemiliano467@gmail.com	12345	987654321	ACTIVO	2	2026-06-12 21:13:34.36847	4
7	Administrador TASA	admin@tasa.pe	admin123	999000111	ACTIVO	1	2026-06-23 19:53:32.170886	\N
8	Luis Mendoza Torres	chofer1@tasa.pe	chofer123	941987654	ACTIVO	4	2026-06-23 22:02:23.196717	\N
9	Carlos Pérez Gonzales	chofer2@tasa.pe	chofer123	943123456	ACTIVO	4	2026-06-23 22:42:38.326899	\N
10	Jorge Ramírez Castillo	chofer3@tasa.pe	chofer123	945112233	ACTIVO	4	2026-06-23 22:42:38.326899	\N
11	Pedro Vásquez Lima	chofer4@tasa.pe	chofer123	940556677	ACTIVO	4	2026-06-23 22:42:38.326899	\N
13	Encargado Almacén	almacen@tasa.pe	almacen123	999111222	ACTIVO	3	2026-06-23 23:35:36.510154	\N
\.


--
-- Data for Name: vehiculo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vehiculo (idvehiculo, placa, marca, modelo, capacidad, estado) FROM stdin;
3	GHI-789	Volkswagen	Constellation	15.00	DISPONIBLE
4	JKL-012	Hino	500 Series	10.00	MANTENIMIENTO
1	ABC-123	Volvo	FH 460	25.00	EN_RUTA
2	DEF-456	Mercedes	Actros	20.00	EN_RUTA
\.


--
-- Name: almacen_idalmacen_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.almacen_idalmacen_seq', 1, true);


--
-- Name: auditoria_idauditoria_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.auditoria_idauditoria_seq', 30, true);


--
-- Name: chofer_idchofer_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chofer_idchofer_seq', 4, true);


--
-- Name: detallepedido_iddetalle_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.detallepedido_iddetalle_seq', 57, true);


--
-- Name: empresa_idempresa_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.empresa_idempresa_seq', 4, true);


--
-- Name: guiadespacho_idguia_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.guiadespacho_idguia_seq', 3, true);


--
-- Name: inventario_idinventario_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inventario_idinventario_seq', 10, true);


--
-- Name: notificacion_idnotificacion_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.notificacion_idnotificacion_seq', 1, false);


--
-- Name: pedido_idpedido_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pedido_idpedido_seq', 16, true);


--
-- Name: producto_idproducto_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.producto_idproducto_seq', 9, true);


--
-- Name: rol_idrol_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rol_idrol_seq', 4, true);


--
-- Name: ruta_idruta_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ruta_idruta_seq', 10, true);


--
-- Name: ticketentrega_idticket_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ticketentrega_idticket_seq', 1, false);


--
-- Name: usuario_idusuario_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuario_idusuario_seq', 13, true);


--
-- Name: vehiculo_idvehiculo_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vehiculo_idvehiculo_seq', 4, true);


--
-- Name: almacen almacen_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.almacen
    ADD CONSTRAINT almacen_pkey PRIMARY KEY (idalmacen);


--
-- Name: auditoria auditoria_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_pkey PRIMARY KEY (idauditoria);


--
-- Name: chofer chofer_dni_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chofer
    ADD CONSTRAINT chofer_dni_key UNIQUE (dni);


--
-- Name: chofer chofer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chofer
    ADD CONSTRAINT chofer_pkey PRIMARY KEY (idchofer);


--
-- Name: detallepedido detallepedido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallepedido
    ADD CONSTRAINT detallepedido_pkey PRIMARY KEY (iddetalle);


--
-- Name: empresa empresa_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empresa
    ADD CONSTRAINT empresa_pkey PRIMARY KEY (idempresa);


--
-- Name: empresa empresa_ruc_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empresa
    ADD CONSTRAINT empresa_ruc_key UNIQUE (ruc);


--
-- Name: guiadespacho guiadespacho_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho
    ADD CONSTRAINT guiadespacho_pkey PRIMARY KEY (idguia);


--
-- Name: inventario inventario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT inventario_pkey PRIMARY KEY (idinventario);


--
-- Name: notificacion notificacion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT notificacion_pkey PRIMARY KEY (idnotificacion);


--
-- Name: pedido pedido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido
    ADD CONSTRAINT pedido_pkey PRIMARY KEY (idpedido);


--
-- Name: producto producto_codigo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT producto_codigo_key UNIQUE (codigo);


--
-- Name: producto producto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT producto_pkey PRIMARY KEY (idproducto);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (idrol);


--
-- Name: ruta ruta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ruta
    ADD CONSTRAINT ruta_pkey PRIMARY KEY (idruta);


--
-- Name: ticketentrega ticketentrega_idguia_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticketentrega
    ADD CONSTRAINT ticketentrega_idguia_key UNIQUE (idguia);


--
-- Name: ticketentrega ticketentrega_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticketentrega
    ADD CONSTRAINT ticketentrega_pkey PRIMARY KEY (idticket);


--
-- Name: usuario usuario_correo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_correo_key UNIQUE (correo);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (idusuario);


--
-- Name: vehiculo vehiculo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehiculo
    ADD CONSTRAINT vehiculo_pkey PRIMARY KEY (idvehiculo);


--
-- Name: vehiculo vehiculo_placa_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehiculo
    ADD CONSTRAINT vehiculo_placa_key UNIQUE (placa);


--
-- Name: idx_auditoria_fecha; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_auditoria_fecha ON public.auditoria USING btree (fecha);


--
-- Name: idx_auditoria_tabla; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_auditoria_tabla ON public.auditoria USING btree (tablaafectada);


--
-- Name: idx_detalle_pedido; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_detalle_pedido ON public.detallepedido USING btree (idpedido);


--
-- Name: idx_detalle_producto; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_detalle_producto ON public.detallepedido USING btree (idproducto);


--
-- Name: idx_guia_chofer; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_guia_chofer ON public.guiadespacho USING btree (idchofer);


--
-- Name: idx_guia_pedido; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_guia_pedido ON public.guiadespacho USING btree (idpedido);


--
-- Name: idx_inventario_almacen; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inventario_almacen ON public.inventario USING btree (idalmacen);


--
-- Name: idx_inventario_producto; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inventario_producto ON public.inventario USING btree (idproducto);


--
-- Name: idx_notif_leido; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_notif_leido ON public.notificacion USING btree (idusuario, leido);


--
-- Name: idx_notif_usuario; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_notif_usuario ON public.notificacion USING btree (idusuario);


--
-- Name: idx_pedido_empresa; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_pedido_empresa ON public.pedido USING btree (idempresa);


--
-- Name: idx_pedido_estado; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_pedido_estado ON public.pedido USING btree (estado);


--
-- Name: idx_pedido_usuario; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_pedido_usuario ON public.pedido USING btree (idusuario);


--
-- Name: idx_usuario_correo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_usuario_correo ON public.usuario USING btree (correo);


--
-- Name: idx_usuario_rol; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_usuario_rol ON public.usuario USING btree (idrol);


--
-- Name: auditoria fk_auditoria_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT fk_auditoria_usuario FOREIGN KEY (idusuario) REFERENCES public.usuario(idusuario);


--
-- Name: detallepedido fk_det_pedido; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallepedido
    ADD CONSTRAINT fk_det_pedido FOREIGN KEY (idpedido) REFERENCES public.pedido(idpedido);


--
-- Name: detallepedido fk_det_producto; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detallepedido
    ADD CONSTRAINT fk_det_producto FOREIGN KEY (idproducto) REFERENCES public.producto(idproducto);


--
-- Name: guiadespacho fk_guia_chofer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho
    ADD CONSTRAINT fk_guia_chofer FOREIGN KEY (idchofer) REFERENCES public.chofer(idchofer);


--
-- Name: guiadespacho fk_guia_pedido; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho
    ADD CONSTRAINT fk_guia_pedido FOREIGN KEY (idpedido) REFERENCES public.pedido(idpedido);


--
-- Name: guiadespacho fk_guia_ruta; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho
    ADD CONSTRAINT fk_guia_ruta FOREIGN KEY (idruta) REFERENCES public.ruta(idruta);


--
-- Name: guiadespacho fk_guia_vehiculo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guiadespacho
    ADD CONSTRAINT fk_guia_vehiculo FOREIGN KEY (idvehiculo) REFERENCES public.vehiculo(idvehiculo);


--
-- Name: inventario fk_inv_almacen; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT fk_inv_almacen FOREIGN KEY (idalmacen) REFERENCES public.almacen(idalmacen);


--
-- Name: inventario fk_inv_producto; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT fk_inv_producto FOREIGN KEY (idproducto) REFERENCES public.producto(idproducto);


--
-- Name: notificacion fk_notif_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT fk_notif_usuario FOREIGN KEY (idusuario) REFERENCES public.usuario(idusuario);


--
-- Name: pedido fk_pedido_empresa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido
    ADD CONSTRAINT fk_pedido_empresa FOREIGN KEY (idempresa) REFERENCES public.empresa(idempresa);


--
-- Name: pedido fk_pedido_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido
    ADD CONSTRAINT fk_pedido_usuario FOREIGN KEY (idusuario) REFERENCES public.usuario(idusuario);


--
-- Name: ticketentrega fk_ticket_guia; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticketentrega
    ADD CONSTRAINT fk_ticket_guia FOREIGN KEY (idguia) REFERENCES public.guiadespacho(idguia);


--
-- Name: usuario fk_usuario_empresa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT fk_usuario_empresa FOREIGN KEY (idempresa) REFERENCES public.empresa(idempresa);


--
-- Name: usuario fk_usuario_rol; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT fk_usuario_rol FOREIGN KEY (idrol) REFERENCES public.rol(idrol);


--
-- PostgreSQL database dump complete
--

\unrestrict AePRsqUmMmldRGwuZ0Yq1YQt3dTE8Qsc9AVDIFwIKeifyVlkZfai5aFKdHDpaHK

