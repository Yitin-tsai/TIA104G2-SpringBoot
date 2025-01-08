//package chilltrip.chat.controller;
//
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
//import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
//
//public class MyWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
//	
//	
//	
//	@Override
//	public WebSocketHandler decorate(WebSocketHandler handler) {
//		// TODO Auto-generated method stub
//		WebSocketHandler Myhandler = new ChatRoomController();
//		return new WebSocketHandlerDecorator(Myhandler) {
//			@Override
//            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//                super.afterConnectionEstablished(session);  // 調用原始 handler 的方法
//                // 如果 session 是 ChatRoomController 相關的 session，就調用 ChatRoomController 的邏輯
//                System.out.println("WebSocket 連線已建立，Session ID: " + session.getId());
//                
//                // 這裡可以調用 ChatRoomController 的方法，例如：
//                if (Myhandler instanceof ChatRoomController) {
//                    ((ChatRoomController) Myhandler).afterConnectionEstablished(session);  // 觸發 ChatRoomController 中的邏輯
//                }
//            }
//
//            @Override
//            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//                super.afterConnectionClosed(session, status);
//                // 如果 session 是 ChatRoomController 相關的 session，就調用 ChatRoomController 的邏輯
//                System.out.println("WebSocket 連線已關閉，Session ID: " + session.getId());
//                
//                // 這裡可以調用 ChatRoomController 的方法，例如：
//                if (Myhandler instanceof ChatRoomController) {
//                    ((ChatRoomController) Myhandler).afterConnectionClosed(session, status);  // 觸發 ChatRoomController 中的邏輯
//                }
//            }
//
//            // 可以根據需要添加其他方法來處理 WebSocket 的消息
//            @Override
//            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//                super.handleMessage(session, message); // 默認處理
//                if (handler instanceof ChatRoomController) {
//                    ((ChatRoomController) handler).handleMessage(session, message);  // 觸發 ChatRoomController 中的邏輯
//                }
//            }
//		};
//	}
//
//}
